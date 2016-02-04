package kr.co.leehana.service.impl;

import kr.co.leehana.model.Player;
import kr.co.leehana.security.UserDetailsImpl;
import kr.co.leehana.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Hana Lee
 * @since 2016-02-04 10:55
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final PlayerService playerService;

	@Autowired
	public UserDetailsServiceImpl(PlayerService playerService) {
		this.playerService = playerService;
	}

	/**
	 * 기본값인 username 대신 email 을 사용하여 UserDetails 를 만든다
	 *
	 * @param email username 대신 email 을 이용
	 * @return UserDetailsImpl 야구게임용 UserDetails
	 * @throws UsernameNotFoundException
	 */
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Player player = playerService.getByEmail(email);
		if (player == null) {
			throw new UsernameNotFoundException(email);
		}
		return new UserDetailsImpl(player);
	}
}
