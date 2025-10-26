import { Router } from "express";
import jwt from "jsonwebtoken";
import { User } from "../models/User.js";
const router = Router();
router.post("/mock-login", async (req, res) => {
  const { name, email } = req.body || {};
  if (!name) return res.status(400).json({ error: "name requerido" });
  let user = await User.findOne({ name, email }).lean();
  if (!user) user = (await User.create({ name, email })).toObject();
  const token = jwt.sign({ id: user._id, name: user.name }, process.env.JWT_SECRET, { expiresIn: "30d" });
  res.json({ token, user });
});
export default router;
