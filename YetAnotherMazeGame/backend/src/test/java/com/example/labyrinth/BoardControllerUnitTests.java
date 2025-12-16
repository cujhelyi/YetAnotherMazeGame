package com.example.labyrinth;

import com.example.labyrinth.controller.BoardController;
import com.example.labyrinth.entity.BoardTile;
import com.example.labyrinth.entity.GameBoard;
import com.example.labyrinth.service.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
public class BoardControllerUnitTests {

    @Mock
    private BoardService boardService;

    private MockMvc mvc;

    @BeforeEach
    void setup() {
        BoardController controller = new BoardController(boardService);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void createBoard_returnsCreatedAndTiles() throws Exception {
        GameBoard gb = new GameBoard();
        gb.resetBoardArray();
        gb.setDefaults();
        // set id and tiles
        // create tiles list (simulate persisted tiles)
        ArrayList<BoardTile> tiles = new ArrayList<>();
        for (int r = 0; r < gb.getBoardSize(); r++) {
            for (int c = 0; c < gb.getBoardSize(); c++) {
                BoardTile t = new BoardTile();
                t.setRowIndex(r);
                t.setColIndex(c);
                t.setTreasure("");
                tiles.add(t);
            }
        }
        gb.clearTiles();
        tiles.forEach(gb::addTile);
        // fake id
        // reflection-free approach: rely on Location header containing the id we return
        when(boardService.createDefaultBoard()).thenAnswer(a -> {
            // set an id via reflection is unnecessary for controller behavior tests
            return gb;
        });

        mvc.perform(post("/api/boards"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.tiles.length()", is(gb.getTiles().size())));
    }

    @Test
    void getBoard_returnsNotFoundWhenMissing() throws Exception {
        when(boardService.loadBoard(anyLong())).thenReturn(null);
        mvc.perform(get("/api/boards/12345"))
                .andExpect(status().isNotFound());
    }
}
