package game.weapons;

import game.Player;

public class Gun {

	private WeaponType type;
	private Player owner;

	private long lastShot;

	public Gun(WeaponType type, Player owner) {
		lastShot = System.currentTimeMillis() - type.getReloadTime();
		this.type = type;
		this.owner = owner;
	}

	public boolean canShoot() {
		return (System.currentTimeMillis() - lastShot >= type.getReloadTime());
	}

	public void shoot(float angle) {
		if(!canShoot()) return;

		for(int i = 0; i < type.getCount(); i++) {

			//TODO: Spawn Bullets

		}
	}

	public WeaponType getWeaponType() {
		return type;
	}

}
