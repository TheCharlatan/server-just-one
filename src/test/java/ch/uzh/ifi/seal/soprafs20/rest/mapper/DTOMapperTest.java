package ch.uzh.ifi.seal.soprafs20.rest.mapper;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.constant.CardStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserUpdateDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyGetDTO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Date;
import java.util.ArrayList;
import java.time.temporal.ChronoUnit;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation works.
 */
public class DTOMapperTest {
    @Test
    public void testCreateUser_fromUserPostDTO_toUser_success() {
        // create UserPostDTO
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("username");
        userPostDTO.setPassword("password");
		userPostDTO.setName("name");
		userPostDTO.setCountry("Burkina Faso");
		userPostDTO.setBirthday(new Date(2017));
		userPostDTO.setGender('f');
		userPostDTO.setImage("image");

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // check content
        assertEquals(userPostDTO.getUsername(), user.getUsername());
        assertEquals(userPostDTO.getPassword(), user.getPassword());
        assertEquals(userPostDTO.getName(), user.getName());
        assertEquals(userPostDTO.getCountry(), user.getCountry());
        assertEquals(userPostDTO.getBirthday(), user.getBirthDay());
        assertEquals(userPostDTO.getGender(), user.getGender());
        assertEquals(userPostDTO.getImage(), user.getImage());
    }

    @Test
    public void testGetUser_fromUser_toUserGetDTO_success() {
        // create User
        User user = new User();
        user.setName("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
		user.setRank(1);
		user.setScore(1);
		user.setCountry("Burkina Faso");
		user.setGender('f');
		user.setBirthDay(new Date(2017));
		user.setToken("this");
		user.setCreationDate(new Date(2017));
		user.setPassword("what");
		user.setGameId(1);
		user.setLobbyId(1);
		user.setInvitations(new ArrayList<Long>());
		user.setImage("image");

        // MAP -> Create UserGetDTO
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        // check content
        assertEquals(user.getId(), userGetDTO.getId());
        assertEquals(user.getName(), userGetDTO.getName());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getStatus(), userGetDTO.getStatus());
    }

	@Test
    public void test_fromGame_toGameGetDTO_success() {
		Game game = new Game();
		game.setId(0l);
		game.setPlayerIds(new ArrayList<Long>());
		game.setRound(0);
		game.setGameStatus(GameStatus.AWAITING_INDEX);
		game.setRoundScore(0);
		game.setActivePlayerId(0l);
		game.setTimestamp(Instant.now().getEpochSecond()-35);
		game.setCardStatus(CardStatus.AWAITING_INDEX);
		game.setWordIndex(0);
		game.setWords(new ArrayList<String>());
		game.setCountAccept(new ArrayList<Long>());

		GameGetDTO gameGetDTO = DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);

		assertEquals(game.getId(), gameGetDTO.getId());
	}

	@Test
	public void test_fromGamePostDTO_toGame_success() {
		GamePostDTO gamePostDTO = new GamePostDTO();
		ArrayList<Long> playerIds = new ArrayList<>();
		playerIds.add(0l);
		gamePostDTO.setPlayerIds(playerIds);

		Game game = DTOMapper.INSTANCE.convertGamePostDTOtoEntity(gamePostDTO);

		assertEquals(gamePostDTO.getPlayerIds(), game.getPlayerIds());
	}

	@Test
	public void test_fromUser_toUserUpdateDTO_success() {
		User user = new User();
		user.setUsername("user");
		user.setName("name");
		user.setGender('f');
		user.setCountry("Burkina Faso");
		user.setBirthDay(new Date(2017));

		UserUpdateDTO userUpdateDTO = DTOMapper.INSTANCE.convertEntityToUserUpdateDTO(user);

		assertEquals(user.getUsername(), userUpdateDTO.getUsername());
	}

	@Test
	public void test_fromLobbyPostDTO_toLobby_success() {
		LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();
		lobbyPostDTO.setName("name");
		lobbyPostDTO.setHostPlayerId(0l);
		lobbyPostDTO.setPlayerIds(new ArrayList<Long>());

		Lobby lobby = DTOMapper.INSTANCE.convertLobbyPostDTOToEntity(lobbyPostDTO);

		assertEquals(lobbyPostDTO.getName(), lobby.getName());
	}

	@Test
	public void test_fromLobby_toLobbyGetDTO_success() {
		Lobby lobby = new Lobby();
		lobby.setId(0l);
		lobby.setName("lol");
		lobby.setGameId(0l);
		lobby.setHostPlayerId(0l);
		lobby.setPlayerIds(new ArrayList<Long>());

		LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);

		assertEquals(lobbyGetDTO.getId(), lobby.getId());
	}
}
