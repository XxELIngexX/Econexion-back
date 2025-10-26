import mongoose from "mongoose";
const { ObjectId } = mongoose.Schema.Types;
const schema = new mongoose.Schema({
  conversationId: { type: ObjectId, ref: "Conversation", index: true, required: true },
  senderId: { type: ObjectId, ref: "User", index: true, required: true },
  text: { type: String, default: "" },
  type: { type: String, enum: ["text","image"], default: "text" },
  status: { type: String, enum: ["sent","delivered","read"], default: "sent", index: true },
  readAt: Date
}, { timestamps: true });
schema.index({ conversationId: 1, createdAt: -1 });
export const Message = mongoose.model("Message", schema);
