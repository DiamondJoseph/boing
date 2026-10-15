// @vitest-environment jsdom
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import type { components } from "./api/schema.js";
import { start } from "./app.js";

type Game = components["schemas"]["Game"];

/**
 * Stands in for the server at the network boundary: answers requests by "METHOD /path" and
 * records them. Everything above fetch (openapi-fetch and the app) is the real code.
 */
class FakeApi {
  readonly requests: Request[] = [];
  private readonly routes = new Map<string, () => Response | Promise<Response>>();

  on(route: string, respond: () => Response | Promise<Response>): this {
    this.routes.set(route, respond);
    return this;
  }

  readonly fetch: typeof globalThis.fetch = async (input) => {
    const request = input as Request;
    this.requests.push(request.clone());
    const route = `${request.method} ${new URL(request.url).pathname}`;
    const respond = this.routes.get(route);
    if (!respond) {
      throw new Error(`Unexpected request: ${route}`);
    }
    return respond();
  };
}

function json(body: unknown, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { "Content-Type": "application/json" },
  });
}

const GAME: Game = {
  id: 7,
  vampire: { id: 1, name: "Vlad", intialDescription: "An old soldier" },
  people: [
    {
      id: 1,
      name: "Ana",
      dead: false,
      immortal: false,
      descriptions: [
        { id: 1, text: "A friend" },
        { id: 2, text: "A rival" },
      ],
    },
    { id: 2, name: "Bo", dead: true, immortal: false, descriptions: [] },
    { id: 3, name: "Cy", dead: false, immortal: true, descriptions: [] },
  ],
  resources: [
    { id: 1, name: "Castle", stationary: true, lost: false },
    { id: 2, name: "Ring", stationary: false, lost: true },
    { id: 3, name: "Horse", stationary: false, lost: false },
  ],
  skills: [
    { id: 1, name: "Fencing", checked: true, lost: false },
    { id: 2, name: "Latin", checked: false, lost: true },
    { id: 3, name: "Riding", checked: false, lost: false },
  ],
  memories: [
    { id: 1, remembered: true, experiences: [{ id: 1, response: "It rained" }] },
    { id: 2, remembered: false, experiences: [] },
  ],
  diaries: [
    { id: 1, description: "Red diary", lost: false, memories: [] },
    { id: 2, description: "Burnt diary", lost: true, memories: [] },
  ],
};

let root: HTMLElement;
let stop: (() => void) | undefined;

/** Sets the hash and waits for the hashchange event, so the app sees exactly one change. */
async function goTo(hash: string): Promise<void> {
  if (window.location.hash === hash || (hash === "" && window.location.hash === "")) {
    return;
  }
  const changed = new Promise((resolve) =>
    window.addEventListener("hashchange", resolve, { once: true }),
  );
  window.location.hash = hash;
  await changed;
}

async function startAt(hash: string, api: FakeApi): Promise<void> {
  await goTo(hash);
  stop = start(root, { fetch: api.fetch });
}

function button(name: string): HTMLButtonElement {
  const found = [...root.querySelectorAll("button")].find((b) => b.textContent === name);
  if (!found) {
    throw new Error(`No button "${name}" in: ${root.textContent}`);
  }
  return found;
}

function section(title: string): HTMLDetailsElement {
  const found = [...root.querySelectorAll<HTMLDetailsElement>("details.holdings")].find((d) =>
    d.querySelector("summary")?.textContent?.startsWith(title),
  );
  if (!found) {
    throw new Error(`No section "${title}"`);
  }
  return found;
}

function items(details: HTMLElement): HTMLLIElement[] {
  return [...details.querySelectorAll<HTMLLIElement>(":scope > ul > li")];
}

function visibleNames(details: HTMLElement): string[] {
  return items(details)
    .filter((li) => !li.hidden)
    .map((li) => li.querySelector(".holding-name")?.textContent ?? "");
}

function item(details: HTMLElement, name: string): HTMLLIElement {
  const found = items(details).find(
    (li) => li.querySelector(".holding-name")?.textContent === name,
  );
  if (!found) {
    throw new Error(`No item "${name}"`);
  }
  return found;
}

function type(input: HTMLInputElement | HTMLTextAreaElement, value: string): void {
  input.value = value;
  input.dispatchEvent(new Event("input"));
}

function toggle(details: HTMLElement, label: string): void {
  const box = [...details.querySelectorAll("label")]
    .find((l) => l.textContent === label)
    ?.querySelector("input");
  if (!box) {
    throw new Error(`No checkbox "${label}"`);
  }
  box.click();
}

/** Fills every field of the new-game form, in order, with distinct values. */
function fillForm(): void {
  const form = root.querySelector("form");
  for (const [i, input] of [...form!.querySelectorAll<HTMLInputElement>("input[type=text]")].entries()) {
    type(input, ` Value ${i} `);
  }
  type(form!.querySelector("textarea")!, "A vampire");
}

beforeEach(async () => {
  sessionStorage.clear();
  await goTo("");
  document.body.innerHTML = '<main id="app"></main>';
  root = document.getElementById("app")!;
  // jsdom does not implement scrolling
  window.scrollTo = () => {};
});

afterEach(() => {
  stop?.();
  stop = undefined;
});

describe("landing page", () => {
  it("lists games as links named after their vampire", async () => {
    const api = new FakeApi().on("GET /api/game", () =>
      json([GAME, { ...GAME, id: 8, vampire: { name: "Carmilla" } }]),
    );
    await startAt("", api);

    await vi.waitFor(() => expect(root.querySelector("table.games")).not.toBeNull());
    const links = [...root.querySelectorAll<HTMLAnchorElement>("table.games a")];
    expect(links.map((a) => [a.textContent, a.getAttribute("href")])).toEqual([
      ["Vlad", "#/game/7"],
      ["Carmilla", "#/game/8"],
    ]);
  });

  it("says when there are no games", async () => {
    await startAt("", new FakeApi().on("GET /api/game", () => json([])));

    await vi.waitFor(() => expect(root.textContent).toContain("No games yet."));
  });

  it("shows server errors", async () => {
    await startAt("", new FakeApi().on("GET /api/game", () => json({}, 500)));

    await vi.waitFor(() => expect(root.querySelector(".error")?.textContent).toContain("500"));
  });

  it("shows network errors", async () => {
    const api = new FakeApi().on("GET /api/game", () => Promise.reject(new TypeError("offline")));
    await startAt("", api);

    await vi.waitFor(() =>
      expect(root.querySelector(".error")?.textContent).toBe("Could not reach the server: offline"),
    );
  });

  it("does not let a slow response replace a newer page", async () => {
    let respond: (response: Response) => void = () => {};
    const api = new FakeApi().on("GET /api/game", () => new Promise((r) => (respond = r)));
    await startAt("", api);

    button("Create a new game").click();
    await vi.waitFor(() => expect(root.querySelector("h1")?.textContent).toBe("New game"));
    respond(json([GAME]));
    await new Promise((r) => setTimeout(r, 0));

    expect(root.querySelector("h1")?.textContent).toBe("New game");
    expect(root.querySelector("table.games")).toBeNull();
  });
});

describe("new game form", () => {
  it("starts with three of everything and enforces the server's limits", async () => {
    await startAt("#/new", new FakeApi());

    const [, people, resources, skills] = [...root.querySelectorAll("fieldset")];
    for (const fieldset of [people, resources, skills]) {
      expect(fieldset!.querySelectorAll(".row")).toHaveLength(3);
      // At the minimum, rows cannot be removed
      expect([...fieldset!.querySelectorAll(".row button")].every((b) => (b as HTMLElement).hidden)).toBe(true);
    }
    expect(people!.querySelector("legend")?.textContent).toBe("People (at least 3)");
    expect(skills!.querySelector("legend")?.textContent).toBe("Skills (exactly 3)");
    // At the maximum, rows cannot be added
    expect(button("Add resource").hidden).toBe(true);
    expect(button("Add skill").hidden).toBe(true);

    button("Add person").click();
    expect(people!.querySelectorAll(".row")).toHaveLength(4);
    expect([...people!.querySelectorAll(".row button")].every((b) => !(b as HTMLElement).hidden)).toBe(true);
  });

  it("creates the game with trimmed values and opens it", async () => {
    const api = new FakeApi().on("POST /api/game", () => json({ ...GAME, id: 42 }, 201));
    await startAt("#/new", api);
    fillForm();
    root.querySelector<HTMLInputElement>("fieldset:nth-of-type(2) input[type=checkbox]")!.click();

    root.querySelector("form")!.requestSubmit();

    await vi.waitFor(() => expect(window.location.hash).toBe("#/game/42"));
    const body = await api.requests[0]!.json();
    expect(body.vampire).toEqual({ name: "Value 0", initialDescription: "A vampire" });
    expect(body.people).toHaveLength(3);
    expect(body.people[0]).toEqual({
      name: "Value 1",
      initialDescription: { description: "Value 2" },
      dead: true,
      immortal: false,
    });
    expect(body.resources[0]).toEqual({ name: "Value 7", stationary: false });
    expect(body.skills).toEqual([{ name: "Value 10" }, { name: "Value 11" }, { name: "Value 12" }]);
    expect(sessionStorage.length).toBe(0);
  });

  it("keeps and later restores the form when creating fails", async () => {
    const api = new FakeApi().on("POST /api/game", () => json({ error: "Bad Request" }, 400));
    await startAt("#/new", api);
    button("Add person").click();
    fillForm();

    root.querySelector("form")!.requestSubmit();

    await vi.waitFor(() =>
      expect(root.querySelector(".error")?.textContent).toContain("Could not create the game (400"),
    );
    expect(button("Create game").disabled).toBe(false);
    expect(root.querySelector<HTMLInputElement>("input[type=text]")!.value).toBe(" Value 0 ");

    // Leave and come back: the form is rebuilt from the saved draft
    await goTo("");
    await goTo("#/new");
    expect(root.textContent).toContain("Restored the game you last tried to create.");
    const values = [...root.querySelectorAll<HTMLInputElement>("input[type=text]")].map((i) => i.value);
    expect(values).toHaveLength(15);
    expect(values[0]).toBe("Value 0");
    expect(values[7]).toBe("Value 7");
    expect(root.querySelector("textarea")!.value).toBe("A vampire");

    button("Discard").click();
    expect(root.textContent).not.toContain("Restored");
    expect(root.querySelector<HTMLInputElement>("input[type=text]")!.value).toBe("");
    expect(sessionStorage.length).toBe(0);
  });

  it("ignores a draft it did not write", async () => {
    sessionStorage.setItem("boing:new-game-draft", JSON.stringify({ vampire: "nonsense" }));
    await startAt("#/new", new FakeApi());

    expect(root.textContent).not.toContain("Restored");
    expect(root.querySelectorAll("input[type=text]")).toHaveLength(13);
  });
});

describe("game page", () => {
  async function openGame(game: Game = GAME): Promise<void> {
    await startAt("#/game/7", new FakeApi().on("GET /api/game/7", () => json(game)));
    await vi.waitFor(() => expect(root.querySelector("h1")?.textContent).toBe("Vlad"));
  }

  it("shows memories and diaries, then people, then skills and resources", async () => {
    await openGame();

    expect(root.querySelector(".vampire-description")?.textContent).toBe("An old soldier");
    const pairs = [...root.querySelectorAll(".pair")].map((pair) =>
      [...pair.querySelectorAll("summary")].map((s) => s.firstChild?.textContent),
    );
    expect(pairs).toEqual([
      ["Memories", "Diaries"],
      ["Skills", "Resources"],
    ]);
    const order = [...root.querySelectorAll("details.holdings summary")].map((s) => s.firstChild?.textContent);
    expect(order).toEqual(["Memories", "Diaries", "People", "Skills", "Resources"]);
  });

  it("hides dead, lost and forgotten items until asked", async () => {
    await openGame();

    expect(visibleNames(section("People"))).toEqual(["Ana", "Cy"]);
    expect(visibleNames(section("Resources"))).toEqual(["Castle", "Horse"]);
    expect(visibleNames(section("Skills"))).toEqual(["Fencing", "Riding"]);
    expect(visibleNames(section("Diaries"))).toEqual(["Red diary"]);
    expect(visibleNames(section("Memories"))).toEqual(["Memory 1"]);
    expect(section("People").querySelector(".count")?.textContent).toBe("2 of 3");

    toggle(section("People"), "Show dead");
    toggle(section("Memories"), "Show forgotten");

    expect(visibleNames(section("People"))).toEqual(["Ana", "Bo", "Cy"]);
    expect(section("People").querySelector(".count")?.textContent).toBe("3");
    expect(visibleNames(section("Memories"))).toEqual(["Memory 1", "Memory 2"]);
  });

  it("strikes through dead and lost items and makes immortal and stationary ones bold", async () => {
    await openGame();
    const nameOf = (title: string, name: string) =>
      item(section(title), name).querySelector(".holding-name")!.classList;

    expect([...nameOf("People", "Bo")]).toContain("struck");
    expect([...nameOf("People", "Cy")]).toContain("bold");
    expect([...nameOf("People", "Ana")]).toEqual(["holding-name"]);
    expect([...nameOf("Resources", "Ring")]).toContain("struck");
    expect([...nameOf("Resources", "Castle")]).toContain("bold");
    expect([...nameOf("Skills", "Latin")]).toContain("struck");
    // The latest description is shown
    expect(item(section("People"), "Ana").textContent).toContain("A rival");
    expect(item(section("People"), "Ana").textContent).not.toContain("A friend");
  });

  it("shows whether skills are checked in read-only checkboxes", async () => {
    await openGame();

    const box = (name: string) => item(section("Skills"), name).querySelector<HTMLInputElement>("input")!;
    expect(box("Fencing").checked).toBe(true);
    expect(box("Riding").checked).toBe(false);
    expect(box("Fencing").getAttribute("aria-label")).toBe("Fencing checked");

    box("Riding").click();
    expect(box("Riding").checked).toBe(false);
  });

  it("filters by name and by state, together with the show toggle", async () => {
    await openGame();
    const people = section("People");
    const filter = people.querySelector<HTMLInputElement>("input[type=search]")!;

    type(filter, "immortal");
    expect(visibleNames(people)).toEqual(["Cy"]);

    type(filter, "dead");
    expect(visibleNames(people)).toEqual([]);
    toggle(people, "Show dead");
    expect(visibleNames(people)).toEqual(["Bo"]);
    expect(people.querySelector(".count")?.textContent).toBe("1 of 3");

    type(filter, "");
    expect(visibleNames(people)).toEqual(["Ana", "Bo", "Cy"]);
  });

  it("says when a game does not exist", async () => {
    // The server answers 200 with a null body for an unknown id
    await startAt("#/game/99", new FakeApi().on("GET /api/game/99", () => json(null)));

    await vi.waitFor(() => expect(root.querySelector(".error")?.textContent).toBe("Game 99 not found."));
    expect(root.querySelector("a")?.getAttribute("href")).toBe("#/");
  });

  it("shows server errors", async () => {
    await startAt("#/game/7", new FakeApi().on("GET /api/game/7", () => json({}, 500)));

    await vi.waitFor(() =>
      expect(root.querySelector(".error")?.textContent).toContain("Could not load the game (500"),
    );
  });
});
