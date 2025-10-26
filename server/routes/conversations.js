import { Router } from "express";
import { Conversation } from "../models/Conversation.js";
const router = Router();
router.get("/", async (req, res) => {
  const userId = req.user.id;
  const items = await Conversation.find({ members: userId })
    .sort({ lastMessageAt: -1 }).limit(50).lean();
  res.json(items);
});
router.post("/", async (req, res) => {
  const userId = req.user.id;
  const { toUserId } = req.body || {};
  if (!toUserId) return res.status(400).json({ error: "toUserId requerido" });
  let conv = await Conversation.findOne({
    members: { $all: [userId, toUserId] }, $expr: { $eq: [{ $size: "$members" }, 2] }
  });
  if (!conv) conv = await Conversation.create({ members: [userId, toUserId], lastMessageAt: new Date() });
  res.json(conv);
});
export default router;
