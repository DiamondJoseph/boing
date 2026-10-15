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

function renderLanding(): void {
  const newGame = el("button", { type: "button" }, "Create a new game");
  newGame.addEventListener("click", renderNewGameForm);
  show(
    el("h1", {}, "Boing"),
    el(
      "p",
      {},
      "Track your journaling RPG games: your vampire, the people they meet, " +
        "the resources they hold and the skills they learn.",
    ),
    newGame,
  );
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
  cancel.addEventListener("click", renderLanding);

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
        renderGameCreated(data);
        return;
      }
      error.textContent =
        `Could not create the game (${response.status} ${response.statusText})` +
        (apiError ? `: ${JSON.stringify(apiError)}` : ".");
    } catch (e) {
      error.textContent = `Could not reach the server: ${e instanceof Error ? e.message : String(e)}`;
    }
    error.hidden = false;
    submit.disabled = false;
  });

  show(el("h1", {}, "New game"), form);
  vampireName.focus();
}

function renderGameCreated(game: Game): void {
  const home = el("button", { type: "button" }, "Back to start");
  home.addEventListener("click", renderLanding);
  const counts = [
    `${game.people?.length ?? 0} people`,
    `${game.resources?.length ?? 0} resources`,
    `${game.skills?.length ?? 0} skills`,
  ].join(", ");
  show(
    el("h1", {}, "Game created"),
    el(
      "p",
      {},
      `Game ${game.id ?? "?"} started for ${game.vampire?.name ?? "your vampire"} with ${counts}.`,
    ),
    home,
  );
}

renderLanding();
