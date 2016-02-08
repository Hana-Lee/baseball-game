package kr.co.leehana.service.impl;

import kr.co.leehana.dto.PlayerDto;
import kr.co.leehana.exception.PlayerNotFoundException;
import kr.co.leehana.exception.PlayerDuplicatedException;
import kr.co.leehana.model.AttackerRoleCount;
import kr.co.leehana.model.DefenderRoleCount;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

		player.setAttackerRoleCount(new AttackerRoleCount(0));
		player.setDefenderRoleCount(new DefenderRoleCount(0));

		final Date now = new Date();
		player.setCreated(now);
		player.setUpdated(now);
	}

	@Override
	public Player updateById(Long id, PlayerDto.Update updateDto) {
		final Player player = getById(id);

		return update(updateDto, player);
	}

	@Override
	public Player updateByEmail(String email, PlayerDto.Update updateDto) {
		final Player player = getByEmail(email);

		return update(updateDto, player);
	}

	private Player update(PlayerDto.Update updateDto, Player player) {
		if (updateDto.getEmail() != null)
			player.setEmail(updateDto.getEmail());

		if (updateDto.getNickname() != null)
			player.setNickname(updateDto.getNickname());

		if (updateDto.getPassword() != null)
			player.setPassword(passwordEncoder.encode(updateDto.getPassword()));

		if (updateDto.getLevel() != null)
			player.setLevel(updateDto.getLevel());

		if (updateDto.getMatchRecord() != null)
			player.setMatchRecord(updateDto.getMatchRecord());

		if (updateDto.getTotalRank() != null)
			player.setTotalRank(updateDto.getTotalRank());

		if (updateDto.getAttackerRoleCount() != null)
			player.setAttackerRoleCount(updateDto.getAttackerRoleCount());

		if (updateDto.getDefenderRoleCount() != null)
			player.setDefenderRoleCount(updateDto.getDefenderRoleCount());

		if (updateDto.getGameRole() != null)
			player.setGameRole(updateDto.getGameRole());

		player.setUpdated(new Date());

		return playerRepository.save(player);
	}

	@Override
	public Player getById(Long id) {
		Player player = playerRepository.findOne(id);
		if (player == null) {
			throw new PlayerNotFoundException(id);
		}

		return player;
	}

	@Override
	public Player getByEmail(String email) {
		Player player = playerRepository.findByEmail(email);
		if (player == null) {
			throw new PlayerNotFoundException(email);
		}

		return player;
	}

	@Override
	public Page<Player> getAll(Pageable pageable) {
		return playerRepository.findAll(pageable);
	}

	@Override
	public List<Player> getAll() {
		return playerRepository.findAll();
	}

	@Override
	public void delete(Long id) {
		playerRepository.delete(getById(id));
	}
}
