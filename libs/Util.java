public class Util {
    public static boolean validateWeapon(String weapon){
	if(weapon.equals("laser") || weapon.equals("mortar") || weapon.equals("droid")){
	    return true;
	}
	return false;
    }
}
