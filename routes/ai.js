import { Router } from "express";
import axios from "axios";

const router = Router();
const AI   = process.env.AI_SEARCH_URL || "http://localhost:5007";
const MOCK = process.env.AI_FAKE === "1";

const delay = (ms) => new Promise(r => setTimeout(r, ms));

// Health
router.get("/health", async (_req, res) => {
  if (MOCK) return res.json({ ok: true, mock: true });
  try {
    const r = await axios.get(`${AI}/health`, { timeout: 2000 });
    res.json({ ...r.data, mock: false });
  } catch {
    res.status(503).json({ ok: false, mock: false, error: "AI service not available" });
  }
});

// Search by text
router.get("/search-text", async (req, res) => {
  try {
    const { q, k } = req.query;
    if (!q) return res.status(400).json({ error: "q requerido" });

    if (MOCK) {
      await delay(120);
      const items = [
        {
          id: "mock-1",
          title: "Botella PET 500ml",
          desc: "plástico transparente",
          image_url: "https://upload.wikimedia.org/wikipedia/commons/thumb/9/90/Plastic_bottle.jpg/320px-Plastic_bottle.jpg",
          score: 0.91
        },
        {
          id: "mock-2",
          title: "Frasco de vidrio ámbar 250ml",
          desc: "reciclado",
          image_url: "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0a/Amber_glass_jar.jpg/320px-Amber_glass_jar.jpg",
          score: 0.82
        }
      ];
      return res.json({ q, k: Number(k ?? 10), items });
    }

    const r = await axios.get(`${AI}/search-by-text`, {
      params: { q, k: k ?? 10 },
      timeout: 5000,
    });
    res.json(r.data);
  } catch (e) {
    res.status(e.response?.status || 503)
       .json({ error: "AI service not available", detail: e.response?.data || e.message });
  }
});

// Index item
router.post("/index-item", async (req, res) => {
  try {
    if (MOCK) {
      await delay(80);
      return res.json({ ok: true, indexed: req.body?.id ?? "mock-indexed", mock: true });
    }
    const r = await axios.post(`${AI}/index-item`, req.body, {
      headers: { "Content-Type": "application/json" },
      timeout: 10000,
    });
    res.json(r.data);
  } catch (e) {
    res.status(e.response?.status || 503)
       .json({ error: "AI service not available", detail: e.response?.data || e.message });
  }
});

export default router;
