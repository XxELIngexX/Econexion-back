import fs from "fs";
import path from "path";

const DATA_PATH = path.resolve("./server/ai/ai_store.json");

function loadDB() {
  if (fs.existsSync(DATA_PATH)) {
    return JSON.parse(fs.readFileSync(DATA_PATH, "utf-8"));
  }
  return { items: [], dim: 0 };
}

function saveDB(db) {
  fs.writeFileSync(DATA_PATH, JSON.stringify(db, null, 2), "utf-8");
}

export function upsertItem({ id, title, desc, vector }) {
  const db = loadDB();
  if (!db.dim) db.dim = vector.length;
  const idx = db.items.findIndex((x) => x.id === id);
  const doc = { id, title, desc, vector: Array.from(vector) };
  if (idx >= 0) db.items[idx] = doc;
  else db.items.push(doc);
  saveDB(db);
  return { ok: true, count: db.items.length, dim: db.dim };
}

function dot(a, b) {
  let s = 0;
  for (let i = 0; i < a.length; i++) s += a[i] * b[i];
  return s; // embeddings normalizados => cos = dot
}

export function searchByTextVector(qVec, k = 10) {
  const db = loadDB();
  if (!db.items.length) return [];
  const scores = db.items.map((it) => ({
    id: it.id,
    title: it.title,
    desc: it.desc,
    score: dot(qVec, it.vector),
  }));
  scores.sort((a, b) => b.score - a.score);
  return scores.slice(0, k);
}
