package kr.co.leehana.service.impl;

import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.exception.PlayerNotFoundException;
import kr.co.leehana.exception.PlayerDuplicatedException;
import kr.co.leehana.model.Player;
import kr.co.leehana.model.Level;
import kr.co.leehana.model.Lose;
import kr.co.leehana.model.MatchRecord;
import kr.co.leehana.model.TotalGame;
import kr.co.leehana.model.TotalRank;
import kr.co.leehana.model.Win;
import kr.co.leehana.repository.PlayerRepository;
import kr.co.leehana.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Hana Lee
 * @since 2016-01-28 17:12
 */
@Service
@Transactional
@Slf4j
public class PlayerServiceImpl implements PlayerService {

	private final PlayerRepository playerRepository;

	private final ModelMapper modelMapper;

	private final PasswordEncoder passwordEncoder;

	@Autowired
	public PlayerServiceImpl(PlayerRepository playerRepository, ModelMapper modelMapper, PasswordEncoder
			passwordEncoder) {
		this.playerRepository = playerRepository;
		this.modelMapper = modelMapper;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Player create(PlayerDto.Create dto) {
		Player player = modelMapper.map(dto, Player.class);
		String email = dto.getEmail();
		if (playerRepository.findByEmail(email) != null) {
			log.error("user duplicated exception. {}", email);
			throw new PlayerDuplicatedException(email);
		}

		fillInitData(player);

		return playerRepository.save(player);
	}

	private void fillInitData(Player player) {
		player.setPassword(passwordEncoder.encode(player.getPassword()));

		player.setAdmin(false);
		player.setLevel(new Level(1));

		final MatchRecord matchRecord = new MatchRecord();
		matchRecord.setLose(new Lose(0));
		matchRecord.setWin(new Win(0));
		matchRecord.setTotalGame(new TotalGame(0));

		player.setMatchRecord(matchRecord);
		player.setTotalRank(new TotalRank(0));

		final Date now = new Date();
		player.setJoined(now);
		player.setUpdated(now);
	}

	@Override
	public Player update(long id, PlayerDto.Update updateDto) {
		final Player player = get(id);
		player.setEmail(updateDto.getEmail());
		player.setNickname(updateDto.getNickname());
		player.setPassword(passwordEncoder.encode(updateDto.getPassword()));
		player.setUpdated(new Date());

		return playerRepository.save(player);
	}

	@Override
	public Player updateStatus(long id, PlayerDto.UpdateStatus updateStatusDto) {
		final Player player = get(id);
		player.setLevel(updateStatusDto.getLevel());
		player.setMatchRecord(updateStatusDto.getMatchRecord());
		player.setTotalRank(updateStatusDto.getTotalRank());
		player.setUpdated(new Date());

		return playerRepository.save(player);
	}

	@Override
	public Player get(long id) {
		Player player = playerRepository.findOne(id);
		if (player == null) {
			throw new PlayerNotFoundException(id);
		}

		return player;
	}

	@Override
	public void delete(long id) {
		playerRepository.delete(get(id));
	}
}
