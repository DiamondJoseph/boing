// Bundles the client into dist/ with esbuild and copies static files from public/.
// Type-checking is done separately by tsc (see the "build" script in package.json).
import { cpSync, rmSync } from "node:fs";
import { build } from "esbuild";

rmSync("dist", { recursive: true, force: true });

await build({
  // version.ts is a separate entry point so that `node dist/version.js` works (see Dockerfile).
  entryPoints: ["src/index.ts", "src/version.ts"],
  outdir: "dist",
  bundle: true,
  format: "esm",
  target: "es2022",
  minify: true,
  sourcemap: true,
  logLevel: "info",
});

cpSync("public", "dist", { recursive: true });
