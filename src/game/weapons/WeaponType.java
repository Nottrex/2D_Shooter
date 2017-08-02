package game.weapons;

public enum WeaponType {

	//TODO: Adjust values
	PISTOL (1, 5, 5, 250L, 1.5f, 1.5f),
	SNIPER (1, 0, 100, 1500L, 1.5f, 1.5f),
	SHOTGUN (5, 50, 1, 4250, 1.5f, 1.5f),
	MACHINE_GUN (1, 15, 7, 50L, 1.5f, 1.5f);


	private int count, spread, speed;
	private float length, width;
	private long reloadTime;
	WeaponType(int bulletCount, int bulletSpread, int bulletSpeed, long reloadTime, float length, float width) {
		this.count = bulletCount;
		this.spread = bulletSpread;
		this.speed = bulletSpeed;

		this.width = width;
		this.length = length;

		this.reloadTime = reloadTime;
	}

	public float getLength() {
		return length;
	}

	public float getWidth() {
		return width;
	}

	public int getCount() {
		return count;
	}

	public int getSpeed() {
		return speed;
	}

	public int getSpread() {
		return spread;
	}

	public long getReloadTime() {
		return reloadTime;
	}
}