import { Router } from "express";
import { Message } from "../models/Message.js";
import { Conversation } from "../models/Conversation.js";
const router = Router();
router.get("/", async (req, res) => {
  const { conversationId, before, limit = 30 } = req.query;
  if (!conversationId) return res.status(400).json({ error: "conversationId requerido" });
  const filter = { conversationId };
  if (before) filter.createdAt = { $lt: new Date(before) };
  const items = await Message.find(filter).sort({ createdAt: -1 }).limit(Number(limit)).lean();
  res.json(items.reverse());
});
router.post("/", async (req, res) => {
  const { conversationId, text } = req.body || {};
  const senderId = req.user.id;
  if (!conversationId || !text) return res.status(400).json({ error: "conversationId y text requeridos" });
  const msg = await Message.create({ conversationId, senderId, text });
  await Conversation.findByIdAndUpdate(conversationId, {
    lastMessage: text, lastSenderId: senderId, lastMessageAt: new Date()
  });
  res.status(201).json(msg);
});
export default router;
