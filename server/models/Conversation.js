import mongoose from "mongoose";
const { ObjectId } = mongoose.Schema.Types;
const schema = new mongoose.Schema({
  members: [{ type: ObjectId, ref: "User", index: true }],
  lastMessage: String,
  lastSenderId: { type: ObjectId, ref: "User" },
  lastMessageAt: { type: Date, default: Date.now }
}, { timestamps: true });
schema.index({ members: 1 });
export const Conversation = mongoose.model("Conversation", schema);
