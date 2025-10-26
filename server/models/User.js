import mongoose from "mongoose";
const schema = new mongoose.Schema({
  name: { type: String, required: true },
  email: { type: String, index: true },
  avatarUrl: String
}, { timestamps: true });
export const User = mongoose.model("User", schema);
