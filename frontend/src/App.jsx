import { useState, useEffect } from 'react';
import './App.css';

export default function App() {
  const [board, setBoard] = useState(null);
  const [spare, setSpare] = useState(null);
  const [currentPlayer, setCurrentPlayer] = useState(1);
  const [phase, setPhase] = useState('insert'); // 'insert' or 'move'
  const backendUrl = import.meta.env.VITE_BACKEND_URL || "http://localhost:8080";

  useEffect(() => {
    loadBoard();
  }, []);

  const loadBoard = async () => {
    try {
      // Try to load board 1 first
      const res = await fetch(`${backendUrl}/api/boards/1`);
      if (res.ok) {
        const data = await res.json();
        setBoard(data);
        setSpare(data.tiles.find(t => t.rowIndex === -1 && t.colIndex === -1));
      } else {
        // Create new board if doesn't exist
        const createRes = await fetch(`${backendUrl}/api/boards`, { method: 'POST' });
        const data = await createRes.json();
        setBoard(data);
        setSpare(data.tiles.find(t => t.rowIndex === -1 && t.colIndex === -1));
      }
    } catch (error) {
      console.error('Error loading board:', error);
    }
  };

  const rotateSpare = async (direction) => {
    try {
      await fetch(`${backendUrl}/api/boards/spare/rotate?direction=${direction}&times=1`, {
        method: 'POST'
      });
      await loadBoard();
    } catch (error) {
      console.error('Error rotating spare:', error);
    }
  };

  const insertSpare = async (row, col, position) => {
    if (phase !== 'insert') {
      alert('You must move a player first!');
      return;
    }
    if (row !== null && ![1, 3, 5].includes(row)) return;
    if (col !== null && ![1, 3, 5].includes(col)) return;
    
    try {
      const params = new URLSearchParams();
      if (row !== null) params.append('row', row);
      if (col !== null) params.append('col', col);
      params.append('position', position);
      
      await fetch(`${backendUrl}/api/boards/${board.id}/insert?${params}`, {
        method: 'POST'
      });
      await loadBoard();
      setPhase('move');
    } catch (error) {
      console.error('Error inserting spare:', error);
    }
  };

  const getReachableTiles = () => {
    if (phase !== 'move' || !board) return new Set();
    
    const player = board.players.find(p => p.playerNumber === currentPlayer);
    if (!player) return new Set();
    
    const startRow = player.rowIndex;
    const startCol = player.colIndex;
    
    // Build grid lookup
    const tileGrid = {};
    board.tiles.forEach(tile => {
      if (tile.rowIndex >= 0 && tile.colIndex >= 0) {
        tileGrid[`${tile.rowIndex},${tile.colIndex}`] = tile;
      }
    });
    
    // Debug: Check if exits exist
    const sampleTile = board.tiles[0];
    if (sampleTile && typeof sampleTile.exitNorth === 'undefined') {
      console.error('Exit data missing from tiles!', sampleTile);
      return new Set([`${startRow},${startCol}`]); // At least allow staying in place
    }
    
    // Helper to check if two tiles are connected
    const areConnected = (tile1Row, tile1Col, tile2Row, tile2Col) => {
      const tile1 = tileGrid[`${tile1Row},${tile1Col}`];
      const tile2 = tileGrid[`${tile2Row},${tile2Col}`];
      
      if (!tile1 || !tile2) return false;
      
      // Check direction from tile1 to tile2
      if (tile2Row === tile1Row - 1 && tile2Col === tile1Col) {
        // tile2 is north of tile1
        return tile1.exitNorth && tile2.exitSouth;
      } else if (tile2Row === tile1Row + 1 && tile2Col === tile1Col) {
        // tile2 is south of tile1
        return tile1.exitSouth && tile2.exitNorth;
      } else if (tile2Row === tile1Row && tile2Col === tile1Col - 1) {
        // tile2 is west of tile1
        return tile1.exitWest && tile2.exitEast;
      } else if (tile2Row === tile1Row && tile2Col === tile1Col + 1) {
        // tile2 is east of tile1
        return tile1.exitEast && tile2.exitWest;
      }
      
      return false;
    };
    
    // BFS to find all reachable tiles
    const reachable = new Set();
    const queue = [[startRow, startCol]];
    const visited = new Set([`${startRow},${startCol}`]);
    reachable.add(`${startRow},${startCol}`);
    
    while (queue.length > 0) {
      const [row, col] = queue.shift();
      
      // Check all four directions
      const directions = [
        [row - 1, col], // north
        [row + 1, col], // south
        [row, col - 1], // west
        [row, col + 1]  // east
      ];
      
      for (const [newRow, newCol] of directions) {
        if (newRow < 0 || newRow > 6 || newCol < 0 || newCol > 6) continue;
        
        const key = `${newRow},${newCol}`;
        if (visited.has(key)) continue;
        
        if (areConnected(row, col, newRow, newCol)) {
          visited.add(key);
          reachable.add(key);
          queue.push([newRow, newCol]);
        }
      }
    }
    
    return reachable;
  };

  const movePlayer = async (row, col) => {
    if (phase !== 'move') {
      alert('You must insert a tile first!');
      return;
    }
    
    // Check if the tile is reachable
    const reachable = getReachableTiles();
    if (!reachable.has(`${row},${col}`)) {
      return; // Silently ignore clicks on unreachable tiles
    }
    
    try {
      const player = board.players.find(p => p.playerNumber === currentPlayer);
      if (!player) return;
      
      await fetch(`${backendUrl}/api/boards/${board.id}/players/${player.id}/move?row=${row}&col=${col}`, {
        method: 'POST'
      });
      await loadBoard();
      
      // Move to next player (1 -> 2 -> 3 -> 4 -> 1)
      setCurrentPlayer(currentPlayer === 4 ? 1 : currentPlayer + 1);
      setPhase('insert');
    } catch (error) {
      console.error('Error moving player:', error);
    }
  };

  const shuffleBoard = async () => {
    try {
      await fetch(`${backendUrl}/api/boards/${board.id}/shuffle`, { method: 'POST' });
      
      // Move all players back to corners
      const corners = [
        { playerNum: 1, row: 0, col: 0 },
        { playerNum: 2, row: 0, col: 6 },
        { playerNum: 3, row: 6, col: 0 },
        { playerNum: 4, row: 6, col: 6 }
      ];
      
      for (const corner of corners) {
        const player = board.players.find(p => p.playerNumber === corner.playerNum);
        if (player) {
          await fetch(`${backendUrl}/api/boards/${board.id}/players/${player.id}/move?row=${corner.row}&col=${corner.col}`, {
            method: 'POST'
          });
        }
      }
      
      await loadBoard();
      setCurrentPlayer(1);
      setPhase('insert');
    } catch (error) {
      console.error('Error shuffling board:', error);
    }
  };

  const canMoveTo = (row, col) => {
    const reachable = getReachableTiles();
    return reachable.has(`${row},${col}`);
  };

  if (!board) return <div>Loading...</div>;

  const gridTiles = board.tiles.filter(t => t.rowIndex >= 0 && t.colIndex >= 0);
  const grid = Array(7).fill(null).map(() => Array(7).fill(null));
  gridTiles.forEach(tile => {
    grid[tile.rowIndex][tile.colIndex] = tile;
  });

  return (
    <div className="app">
      <h1>Labyrinth Board Game</h1>
      
      <div className="controls">
        <button onClick={shuffleBoard}>Shuffle Board</button>
        <div className="current-player">
          Player {currentPlayer} - {phase === 'insert' ? 'Insert Tile' : 'Move Player'}
        </div>
      </div>

      <div className="spare-tile-section">
        <h3>Spare Tile</h3>
        <div className="spare-controls">
          <button onClick={() => rotateSpare('COUNTERCLOCKWISE')}>↺ CCW</button>
          {spare && (
            <div className="tile">
              <img 
                src={`/tiles/${spare.image}`} 
                alt="spare tile"
                style={{ transform: `rotate(${spare.rotation}deg)` }}
              />
              {spare.treasure && spare.treasure !== 'NONE' && (
                <div className="treasure-label">{spare.treasure}</div>
              )}
            </div>
          )}
          <button onClick={() => rotateSpare('CLOCKWISE')}>↻ CW</button>
        </div>
      </div>

      <div className="game-board-wrapper">
        {/* Top insertion buttons */}
        <div className="insert-buttons-top">
          {[0, 1, 2, 3, 4, 5, 6].map(col => (
            <button 
              key={`top-${col}`}
              className="insert-btn insert-btn-vertical"
              onClick={() => insertSpare(null, col, 'start')}
              style={{ visibility: [1, 3, 5].includes(col) ? 'visible' : 'hidden' }}
            >
              ↓
            </button>
          ))}
        </div>

        <div className="game-board-with-sides">
          {/* Left insertion buttons */}
          <div className="insert-buttons-left">
            {[0, 1, 2, 3, 4, 5, 6].map(row => (
              <button 
                key={`left-${row}`}
                className="insert-btn insert-btn-horizontal"
                onClick={() => insertSpare(row, null, 'start')}
                style={{ visibility: [1, 3, 5].includes(row) ? 'visible' : 'hidden' }}
              >
                →
              </button>
            ))}
          </div>

          {/* Game board */}
          <div className="game-board">
            {grid.map((row, rowIndex) => (
              <div key={rowIndex} className="board-row">
                {row.map((tile, colIndex) => {
                  const playersOnTile = board.players?.filter(p => p.rowIndex === rowIndex && p.colIndex === colIndex) || [];
                  const isHighlighted = canMoveTo(rowIndex, colIndex);
                  return (
                    <div 
                      key={colIndex} 
                      className={`tile ${isHighlighted ? 'highlighted' : ''}`}
                      onClick={() => movePlayer(rowIndex, colIndex)}
                      style={{ cursor: 'pointer' }}
                    >
                      {tile && (
                        <>
                          <img 
                            src={`/tiles/${tile.image}`} 
                            alt={`tile ${tile.rowIndex},${tile.colIndex}`}
                            style={{ transform: `rotate(${tile.rotation}deg)` }}
                          />
                          {tile.treasure && tile.treasure !== 'NONE' && (
                            <div className="treasure-label">{tile.treasure}</div>
                          )}
                          {playersOnTile.length > 0 && (
                            <div className="players-container">
                              {playersOnTile.map(player => (
                                <img 
                                  key={player.playerNumber}
                                  src={`/tiles/P${player.playerNumber}.png`}
                                  alt={`Player ${player.playerNumber}`}
                                  className="player-icon"
                                  data-player={player.playerNumber}
                                />
                              ))}
                            </div>
                          )}
                        </>
                      )}
                    </div>
                  );
                })}
              </div>
            ))}
          </div>

          {/* Right insertion buttons */}
          <div className="insert-buttons-right">
            {[0, 1, 2, 3, 4, 5, 6].map(row => (
              <button 
                key={`right-${row}`}
                className="insert-btn insert-btn-horizontal"
                onClick={() => insertSpare(row, null, 'end')}
                style={{ visibility: [1, 3, 5].includes(row) ? 'visible' : 'hidden' }}
              >
                ←
              </button>
            ))}
          </div>
        </div>

        {/* Bottom insertion buttons */}
        <div className="insert-buttons-bottom">
          {[0, 1, 2, 3, 4, 5, 6].map(col => (
            <button 
              key={`bottom-${col}`}
              className="insert-btn insert-btn-vertical"
              onClick={() => insertSpare(null, col, 'end')}
              style={{ visibility: [1, 3, 5].includes(col) ? 'visible' : 'hidden' }}
            >
              ↑
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}

