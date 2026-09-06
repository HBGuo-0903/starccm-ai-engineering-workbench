export function flattenWithMap(html){
  let s = html, map = Array.from({length: html.length}, (_, i) => i);
  function apply(regex, rep){
    const out = [], om = []; let last = 0;
    for (const m of s.matchAll(regex)) {
      for (let i = last; i < m.index; i++) { out.push(s[i]); om.push(map[i]); }
      for (const ch of rep) { out.push(ch); om.push(map[m.index]); }
      last = m.index + m[0].length;
    }
    for (let i = last; i < s.length; i++) { out.push(s[i]); om.push(map[i]); }
    s = out.join(""); map = om;
  }
  apply(/<script[\s\S]*?<\/script>/gi, " ");
  apply(/<style[\s\S]*?<\/style>/gi, " ");
  apply(/<[^>]+>/g, " ");
  apply(/&nbsp;/g, " ");
  apply(/&lt;/g, "<");
  apply(/&gt;/g, ">");
  apply(/&amp;/g, "&");
  apply(/\s+/g, " ");
  let a = 0, b = s.length;
  while (a < b && /\s/.test(s[a])) a++;
  while (b > a && /\s/.test(s[b - 1])) b--;
  return { text: s.slice(a, b), map: map.slice(a, b) };
}
