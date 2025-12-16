import { useState, useEffect } from 'react';

export default function App() {
  const [players, setPlayers] = useState([]);
  const [name, setName] = useState('');

  const backendUrl = import.meta.env.VITE_BACKEND_URL || "http://localhost:8080";

  useEffect(() => {
    fetch(`${backendUrl}/api/game/players`)
      .then(res => res.json())
      .then(setPlayers)
      .catch(console.error);
  }, []);

  const joinGame = async () => {
    await fetch(`${backendUrl}/api/game/join?name=${name}`, { method: "POST" });
    const res = await fetch(`${backendUrl}/api/game/players`);
    setPlayers(await res.json());
    setName('');
  };

  const resetGame = async () => {
    await fetch(`${backendUrl}/api/game/reset`, { method: "DELETE" });
    setPlayers([]);
  };

  return (
    <main style={{ padding: "2rem", fontFamily: "sans-serif" }}>
      <h1>🌀 Labyrinth Multiplayer Demo</h1>
      <div>
        <input
          value={name}
          onChange={e => setName(e.target.value)}
          placeholder="Enter your name"
        />
        <button onClick={joinGame}>Join</button>
        <button onClick={resetGame}>Reset</button>
      </div>
      <h2>Players:</h2>
      <ul>
        {players.map(p => <li key={p}>{p}</li>)}
      </ul>
    </main>
  );
}

