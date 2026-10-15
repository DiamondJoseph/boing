import createClient from "openapi-fetch";
import type { components, paths } from "./api/schema.js";

type Schemas = components["schemas"];
type NewGame = Schemas["NewGame"];
type NewPerson = Schemas["NewPerson"];
type NewResource = Schemas["NewResource"];
type NewSkill = Schemas["NewSkill"];
type Game = Schemas["Game"];

// The client is served by the same server that hosts the API.
const client = createClient<paths>({ baseUrl: window.location.origin });

const root = document.getElementById("app");
if (!root) {
  throw new Error("Missing #app element");
}
const app: HTMLElement = root;

type Child = Node | string;

/** Create an element, assign properties to it and append children. */
function el<K extends keyof HTMLElementTagNameMap>(
  tag: K,
  props: Partial<HTMLElementTagNameMap[K]> = {},
  ...children: Child[]
): HTMLElementTagNameMap[K] {
  const element = Object.assign(document.createElement(tag), props);
  element.append(...children);
  return element;
}

function show(...children: Child[]): void {
  app.replaceChildren(...children);
}

function textInput(label: string, required = true): [HTMLLabelElement, HTMLInputElement] {
  const input = el("input", { type: "text", required });
  return [el("label", {}, label, input), input];
}

function checkbox(label: string): [HTMLLabelElement, HTMLInputElement] {
  const input = el("input", { type: "checkbox" });
  return [el("label", { className: "checkbox" }, input, label), input];
}

interface Row<T> {
  fields: Child[];
  read: () => T;
}

/**
 * A fieldset holding between `min` and `max` rows, each built by `makeRow`.
 * It starts with `min` rows. `values` reads the current rows in display order.
 * The limits mirror the validation in the server's `Game` constructor.
 */
function listSection<T>(
  legend: string,
  addLabel: string,
  makeRow: () => Row<T>,
  min: number,
  max = Infinity,
): { element: HTMLFieldSetElement; values: () => T[] } {
  const rows = new Map<HTMLElement, { read: () => T; remove: HTMLButtonElement }>();
  const container = el("div", { className: "rows" });
  const add = el("button", { type: "button", className: "secondary" }, addLabel);

  const updateControls = (): void => {
    add.hidden = rows.size >= max;
    for (const { remove } of rows.values()) {
      remove.hidden = rows.size <= min;
    }
  };

  const addRow = (): HTMLElement => {
    const { fields, read } = makeRow();
    const row = el("div", { className: "row" }, ...fields);
    const remove = el("button", { type: "button", className: "secondary" }, "Remove");
    remove.addEventListener("click", () => {
      rows.delete(row);
      row.remove();
      updateControls();
    });
    row.append(remove);
    rows.set(row, { read, remove });
    container.append(row);
    updateControls();
    return row;
  };

  add.addEventListener("click", () => addRow().querySelector("input")?.focus());
  for (let i = 0; i < min; i++) {
    addRow();
  }

  const hint = max === min ? `exactly ${min}` : max === Infinity ? `at least ${min}` : `${min} to ${max}`;
  return {
    element: el("fieldset", {}, el("legend", {}, `${legend} (${hint})`), container, add),
    values: () => [...rows.values()].map(({ read }) => read()),
  };
}

function personRow(): Row<NewPerson> {
  const [nameLabel, name] = textInput("Name");
  const [descriptionLabel, description] = textInput("Description");
  const [deadLabel, dead] = checkbox("Dead");
  const [immortalLabel, immortal] = checkbox("Immortal");
  return {
    fields: [nameLabel, descriptionLabel, deadLabel, immortalLabel],
    read: () => ({
      name: name.value.trim(),
      initialDescription: { description: description.value.trim() },
      dead: dead.checked,
      immortal: immortal.checked,
    }),
  };
}

function resourceRow(): Row<NewResource> {
  const [nameLabel, name] = textInput("Name");
  const [stationaryLabel, stationary] = checkbox("Stationary");
  return {
    fields: [nameLabel, stationaryLabel],
    read: () => ({ name: name.value.trim(), stationary: stationary.checked }),
  };
}

function skillRow(): Row<NewSkill> {
  const [nameLabel, name] = textInput("Name");
  return {
    fields: [nameLabel],
    read: () => ({ name: name.value.trim() }),
  };
}

function describeError(e: unknown): string {
  return `Could not reach the server: ${e instanceof Error ? e.message : String(e)}`;
}

function describeFailure(what: string, response: Response, apiError: unknown): string {
  return (
    `Could not ${what} (${response.status} ${response.statusText})` +
    (apiError ? `: ${JSON.stringify(apiError)}` : ".")
  );
}

function navigate(hash: string): void {
  window.location.hash = hash;
}

function gameHash(id: number): string {
  return `#/game/${id}`;
}

async function renderLanding(isCurrent: () => boolean): Promise<void> {
  const newGame = el("button", { type: "button" }, "Create a new game");
  newGame.addEventListener("click", () => navigate("#/new"));
  const games = el("div", {}, el("p", {}, "Loading games…"));
  show(
    el("h1", {}, "Boing"),
    el(
      "p",
      {},
      "Track your journaling RPG games: your vampire, the people they meet, " +
        "the resources they hold and the skills they learn.",
    ),
    games,
    newGame,
  );

  let content: Child;
  try {
    const { data, error, response } = await client.GET("/api/game");
    content = data
      ? gamesTable(data)
      : el("p", { className: "error" }, describeFailure("load games", response, error));
  } catch (e) {
    content = el("p", { className: "error" }, describeError(e));
  }
  if (isCurrent()) {
    games.replaceChildren(content);
  }
}

function gamesTable(games: Game[]): HTMLElement {
  if (games.length === 0) {
    return el("p", {}, "No games yet.");
  }
  const rows = games.map((game) => {
    const name = game.vampire?.name ?? "Unnamed vampire";
    const cell = game.id === undefined ? name : el("a", { href: gameHash(game.id) }, name);
    return el("tr", {}, el("td", {}, cell));
  });
  return el(
    "table",
    { className: "games" },
    el("thead", {}, el("tr", {}, el("th", { scope: "col" }, "Vampire"))),
    el("tbody", {}, ...rows),
  );
}

interface Holding {
  name: string;
  /** Shown as badges after the name. */
  tags?: string[];
  /**
   * States shown only through styling (struck/bold/checkbox). They are also added as
   * visually hidden text so screen readers announce them and the filter can match them.
   */
  states?: string[];
  struck?: boolean;
  bold?: boolean;
  /** When set, a read-only checkbox showing this value is shown before the name. */
  checked?: boolean;
  /** Hidden by default (e.g. dead or lost); see `showInactiveLabel` in `holdingSection`. */
  inactive?: boolean;
  details?: Child[];
}

/** A checkbox that shows a value but can't be changed. */
function readOnlyCheckbox(checked: boolean, label: string): HTMLInputElement {
  const box = el("input", { type: "checkbox", checked, className: "read-only" });
  box.setAttribute("aria-readonly", "true");
  box.setAttribute("aria-label", label);
  // Unlike disabled, this keeps the box at full contrast and focusable.
  box.addEventListener("click", (event) => event.preventDefault());
  return box;
}

/**
 * A collapsible list of holdings with a text filter. The filter matches against everything
 * in an item's text, including its tags and states, so typing "lost" shows only lost items.
 * Inactive items are hidden until the `showInactiveLabel` checkbox is ticked.
 */
function holdingSection<T>(
  title: string,
  items: T[],
  describe: (item: T) => Holding,
  showInactiveLabel: string,
): HTMLElement {
  const entries = items.map((item) => {
    const { name, tags = [], states = [], struck, bold, checked, inactive, details = [] } =
      describe(item);
    const nameClasses = ["holding-name", ...(struck ? ["struck"] : []), ...(bold ? ["bold"] : [])];
    const li = el(
      "li",
      {},
      ...(checked === undefined ? [] : [readOnlyCheckbox(checked, `${name} checked`)]),
      el("span", { className: nameClasses.join(" ") }, name),
      ...(states.length ? [el("span", { className: "visually-hidden" }, ` (${states.join(", ")})`)] : []),
      ...tags.map((tag) => el("span", { className: "tag" }, tag)),
      ...details,
    );
    return { li, inactive: inactive ?? false };
  });

  const count = el("span", { className: "count" });
  const setCount = (shown: number): void => {
    count.textContent = shown === items.length ? `${items.length}` : `${shown} of ${items.length}`;
  };
  setCount(items.length);

  const summary = el("summary", {}, title, " ", count);
  if (items.length === 0) {
    return el("details", { className: "holdings" }, summary, el("p", {}, `No ${title.toLowerCase()}.`));
  }

  const filter = el("input", { type: "search", placeholder: `Filter ${title.toLowerCase()}` });
  filter.setAttribute("aria-label", `Filter ${title.toLowerCase()}`);
  const [showInactiveToggle, showInactive] = checkbox(showInactiveLabel);

  const applyFilters = (): void => {
    const query = filter.value.trim().toLowerCase();
    let shown = 0;
    for (const { li, inactive } of entries) {
      li.hidden =
        (inactive && !showInactive.checked) ||
        !(li.textContent ?? "").toLowerCase().includes(query);
      shown += li.hidden ? 0 : 1;
    }
    setCount(shown);
  };
  filter.addEventListener("input", applyFilters);
  showInactive.addEventListener("change", applyFilters);
  applyFilters();

  return el(
    "details",
    { className: "holdings", open: true },
    summary,
    el("div", { className: "filter-bar" }, filter, showInactiveToggle),
    el("ul", {}, ...entries.map(({ li }) => li)),
  );
}

function flags(...entries: [boolean | undefined, string][]): string[] {
  return entries.filter(([set]) => set).map(([, tag]) => tag);
}

async function renderGame(id: number, isCurrent: () => boolean): Promise<void> {
  show(el("p", {}, "Loading game…"));

  let game: Game;
  try {
    const { data, error, response } = await client.GET("/api/game/{id}", {
      params: { path: { id } },
    });
    if (!isCurrent()) {
      return;
    }
    if (!data) {
      // The server answers 200 with a null body for an unknown id.
      const message = response.ok
        ? `Game ${id} not found.`
        : describeFailure("load the game", response, error);
      show(backLink(), el("p", { className: "error" }, message));
      return;
    }
    game = data;
  } catch (e) {
    if (isCurrent()) {
      show(backLink(), el("p", { className: "error" }, describeError(e)));
    }
    return;
  }

  const description = game.vampire?.intialDescription;
  const memories = holdingSection("Memories", game.memories ?? [], (memory) => ({
    name: `Memory ${memory.id ?? ""}`.trim(),
    tags: flags([memory.remembered === false, "Forgotten"]),
    inactive: memory.remembered === false,
    details: [
      el(
        "ul",
        {},
        ...(memory.experiences ?? []).map((experience) =>
          el("li", {}, experience.response ?? ""),
        ),
      ),
    ],
  }), "Show forgotten");
  const diaries = holdingSection("Diaries", game.diaries ?? [], (diary) => ({
    name: diary.description ?? "Untitled diary",
    tags: flags([diary.lost, "Lost"]),
    details: [el("p", {}, `${diary.memories?.length ?? 0} memories`)],
    inactive: diary.lost ?? false,
  }), "Show lost");
  const people = holdingSection("People", game.people ?? [], (person) => {
    const latest = person.descriptions?.at(-1)?.text;
    return {
      name: person.name ?? "Unnamed",
      states: flags([person.dead, "Dead"], [person.immortal, "Immortal"]),
      struck: person.dead ?? false,
      bold: person.immortal ?? false,
      inactive: person.dead ?? false,
      ...(latest ? { details: [el("p", {}, latest)] } : {}),
    };
  }, "Show dead");
  const skills = holdingSection("Skills", game.skills ?? [], (skill) => ({
    name: skill.name ?? "Unnamed",
    // "Checked" stays in the text so the filter can find checked skills.
    states: flags([skill.checked, "Checked"], [skill.lost, "Lost"]),
    struck: skill.lost ?? false,
    checked: skill.checked ?? false,
    inactive: skill.lost ?? false,
  }), "Show lost");
  const resources = holdingSection("Resources", game.resources ?? [], (resource) => ({
    name: resource.name ?? "Unnamed",
    states: flags([resource.stationary, "Stationary"], [resource.lost, "Lost"]),
    struck: resource.lost ?? false,
    bold: resource.stationary ?? false,
    inactive: resource.lost ?? false,
  }), "Show lost");

  show(
    backLink(),
    el("h1", {}, game.vampire?.name ?? "Unnamed vampire"),
    ...(description ? [el("p", { className: "vampire-description" }, description)] : []),
    el("div", { className: "pair" }, memories, diaries),
    people,
    el("div", { className: "pair" }, skills, resources),
  );
}

function backLink(): HTMLElement {
  return el("p", {}, el("a", { href: "#/" }, "← All games"));
}

function renderNewGameForm(): void {
  const [vampireNameLabel, vampireName] = textInput("Name");
  const vampireDescription = el("textarea", { required: true, rows: 4 });

  const people = listSection("People", "Add person", personRow, 3);
  const resources = listSection("Resources", "Add resource", resourceRow, 3, 3);
  const skills = listSection("Skills", "Add skill", skillRow, 3, 3);

  const error = el("p", { className: "error", hidden: true });
  const submit = el("button", { type: "submit" }, "Create game");
  const cancel = el("button", { type: "button", className: "secondary" }, "Cancel");
  cancel.addEventListener("click", () => navigate("#/"));

  const form = el(
    "form",
    {},
    el(
      "fieldset",
      {},
      el("legend", {}, "Vampire"),
      vampireNameLabel,
      el("label", {}, "Description", vampireDescription),
    ),
    people.element,
    resources.element,
    skills.element,
    error,
    el("div", { className: "actions" }, submit, cancel),
  );

  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    const body: NewGame = {
      vampire: {
        name: vampireName.value.trim(),
        initialDescription: vampireDescription.value.trim(),
      },
      people: people.values(),
      resources: resources.values(),
      skills: skills.values(),
    };

    submit.disabled = true;
    error.hidden = true;
    try {
      const { data, error: apiError, response } = await client.POST("/api/game", { body });
      if (data) {
        navigate(data.id === undefined ? "#/" : gameHash(data.id));
        return;
      }
      error.textContent = describeFailure("create the game", response, apiError);
    } catch (e) {
      error.textContent = describeError(e);
    }
    error.hidden = false;
    submit.disabled = false;
  });

  show(el("h1", {}, "New game"), form);
  vampireName.focus();
}

// Routes live in the URL hash (e.g. #/game/1) so that the server only ever has to serve
// index.html, and the browser's back button and bookmarks work.
let routeCount = 0;

function route(): void {
  const current = ++routeCount;
  // Async pages check this before rendering, so a slow response can't replace a newer page.
  const isCurrent = (): boolean => current === routeCount;
  const hash = window.location.hash;
  const gameMatch = /^#\/game\/(\d+)$/.exec(hash);

  if (gameMatch?.[1]) {
    void renderGame(Number(gameMatch[1]), isCurrent);
  } else if (hash === "#/new") {
    renderNewGameForm();
  } else {
    void renderLanding(isCurrent);
  }
  window.scrollTo(0, 0);
}

window.addEventListener("hashchange", route);
route();
