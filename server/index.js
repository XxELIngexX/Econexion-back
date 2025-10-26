import "dotenv/config.js";
import express from "express";
import http from "http";
import cors from "cors";
import morgan from "morgan";
import { Server } from "socket.io";
import { connectDB } from "./db.js";
import { auth } from "./middleware/auth.js";
import authRoutes from "./routes/auth.js";
import convRoutes from "./routes/conversations.js";
import msgRoutes from "./routes/messages.js";
import { Message } from "./models/Message.js";
import { Conversation } from "./models/Conversation.js";
import jwt from "jsonwebtoken";

const app = express();
app.use(cors({ origin: (process.env.CORS_ORIGIN || "*").split(",") }));
app.use(express.json());
app.use(morgan("dev"));

app.get("/health", (_, res) => res.json({ ok: true }));

app.use("/auth", authRoutes);              // solo dev
app.use("/conversations", auth, convRoutes);
app.use("/messages", auth, msgRoutes);

const server = http.createServer(app);
const io = new Server(server, { cors: { origin: (process.env.CORS_ORIGIN || "*").split(",") } });

io.use((socket, next) => {
  try {
    const raw = socket.handshake.auth?.token || socket.handshake.headers.authorization || "";
    const token = raw.startsWith("Bearer ") ? raw.slice(7) : raw;
    const payload = jwt.verify(token, process.env.JWT_SECRET);
    socket.data.userId = payload.id;
    next();
  } catch {
    next(new Error("unauthorized"));
  }
});

io.on("connection", (socket) => {
  const userId = socket.data.userId;
  socket.join(`user:${userId}`);

  socket.on("chat:join", ({ conversationId }) => {
    if (conversationId) socket.join(`conv:${conversationId}`);
  });

  socket.on("chat:typing", ({ conversationId, isTyping }) => {
    socket.to(`conv:${conversationId}`).emit("chat:typing", { userId, isTyping: !!isTyping });
  });

  socket.on("chat:send", async ({ conversationId, text }) => {
    if (!conversationId || !text) return;
    const msg = await Message.create({ conversationId, senderId: userId, text });
    await Conversation.findByIdAndUpdate(conversationId, {
      lastMessage: text, lastSenderId: userId, lastMessageAt: new Date()
    });
    io.to(`conv:${conversationId}`).emit("chat:message", msg);
  });

  socket.on("chat:read", async ({ conversationId, messageIds = [] }) => {
    await Message.updateMany({ _id: { $in: messageIds } }, { status: "read", readAt: new Date() });
    io.to(`conv:${conversationId}`).emit("chat:read", { conversationId, messageIds });
  });
});

const PORT = process.env.PORT || 4000;
connectDB().then(() =>
  server.listen(PORT, () => console.log(`API+WS http://localhost:${PORT}`))
);
