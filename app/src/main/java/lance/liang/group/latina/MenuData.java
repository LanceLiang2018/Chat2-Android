package lance.liang.group.latina;
import java.util.*;
import com.bumptech.glide.module.*;

public class MenuData
{
	public static class ID {
		final public static int LEFT_ME = 9, LEFT_PRINTER = 1, LEFT_PEOPLE = 2, LEFT_SETTINGS = 3, 
		LEFT_MORE = 4, LEFT_HELP = 5, LEFT_ABOUT = 6,
		ME_MY_INFO = 7, ME_SET_INFO = 8;
	};
	
	public static enum LeftMenu {
		Me(new ItemBeanLeft(R.drawable.icon_people, "我"), ID.LEFT_ME),
		Printer(new ItemBeanLeft(R.drawable.icon_printer, "打印机"), ID.LEFT_PRINTER),
		People(new ItemBeanLeft(R.drawable.icon_people, "联系人"), ID.LEFT_PEOPLE),
		Settings(new ItemBeanLeft(R.drawable.icon_settings, "设置"), ID.LEFT_SETTINGS),
		More(new ItemBeanLeft(R.drawable.icon_music_downloader, "更多"), ID.LEFT_MORE),
		About(new ItemBeanLeft(R.drawable.icon_printer, "关于"), ID.LEFT_ABOUT);
		public ItemBeanLeft item;
		public int id;
		public LeftMenu(ItemBeanLeft item, int id) {
			this.item = item;
			this.id = id;
		}
	};
	
	public static enum Settings {
		MyInfo(new ItemBeanSettings("我的信息"), ID.ME_MY_INFO),
		SetInfo(new ItemBeanSettings("信息设置"), ID.ME_SET_INFO);
		ItemBeanSettings item;
		int id;
		public Settings(ItemBeanSettings item, int id) {
			this.item = item;
			this.id = id;
		}
	}
	
	public static LeftMenu[] listLeft = {
		LeftMenu.Me, LeftMenu.Printer, LeftMenu.People, LeftMenu.Settings, LeftMenu.More, LeftMenu.About
	};
	
	public static Settings[] listMe = {
		Settings.MyInfo, Settings.SetInfo
	};
}
