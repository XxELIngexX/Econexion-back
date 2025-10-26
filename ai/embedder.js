import { pipeline } from "@xenova/transformers";

let _pipePromise = null;

export async function getTextEmbedder() {
  if (!_pipePromise) {
    _pipePromise = pipeline("feature-extraction", "Xenova/all-MiniLM-L6-v2");
  }
  return _pipePromise;
}

export async function embedTexts(texts) {
  const arr = Array.isArray(texts) ? texts : [texts];
  const pipe = await getTextEmbedder();
  const out = await pipe(arr, { pooling: "mean", normalize: true });
  const [batch, dim] = out.dims.slice(-2);
  const data = out.data; // Float32Array
  const rows = [];
  for (let i = 0; i < batch; i++) {
    rows.push(data.slice(i * dim, (i + 1) * dim));
  }
  return rows; // [ Float32Array, ... ]
}
