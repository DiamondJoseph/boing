import createClient from "openapi-fetch";
import type { paths } from "./api/schema.js";

const client = createClient<paths>({ baseUrl: "https://myapi.dev/v1/" });
