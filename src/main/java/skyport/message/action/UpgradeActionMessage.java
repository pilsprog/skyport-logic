package skyport.message.action;

public class UpgradeActionMessage extends ActionMessage {
    private String weapon;
    
    public UpgradeActionMessage(String weapon) {
       this.weapon = weapon;
    }

    public String getWeaponName() {
        return weapon;
    }
}
