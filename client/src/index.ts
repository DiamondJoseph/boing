import { start } from "./app.js";

const root = document.getElementById("app");
if (!root) {
  throw new Error("Missing #app element");
}
start(root);
