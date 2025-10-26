import "dotenv/config";
import express from "express";
import cors from "cors";
import aiRoutes from "./routes/ai.js";

const app = express();

app.use((req, _res, next) => { console.log(`${req.method} ${req.url}`); next(); });
app.use(cors());
app.use(express.json());

// Health básico del API
app.get("/health", (_req, res) => res.send("  ok\n  --\nTrue\n"));

// 👇 MONTA el router IA
app.use("/ai", aiRoutes);

// 404 legible
app.use((req, res) => res.status(404).send(`No route: ${req.method} ${req.url}`));

const PORT = process.env.PORT || 4000;
app.listen(PORT, () => console.log(`API http://localhost:${PORT}`));
