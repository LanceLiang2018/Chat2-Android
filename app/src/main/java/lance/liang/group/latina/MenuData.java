package lance.liang.group.latina;
import java.util.*;
import com.bumptech.glide.module.*;

public class MenuData
{
	public static class ID {
		final public static int LEFT_ME = 9, LEFT_PRINTER = 1, LEFT_PEOPLE = 2, LEFT_SETTINGS = 3, 
		LEFT_MORE = 4, LEFT_HELP = 5, LEFT_ABOUT = 6,
		ME_MY_INFO = 7, ME_SET_INFO = 8, ME_LOGOUT = 13, ME_LOGIN = 19,
		ME_MAKE_FRIENDS = 14, ME_NEW_ROOM = 15, ME_NEW_USER = 16,
		SETTINGS_THEME = 9, SETTINGS_SPLASH = 10, SETTINGS_FONT = 11, SETTINGS_SERVER = 12, SETTINGS_BG = 21, 
		PRINTER_DEFAULT = 17, PRINTER_ADD = 18,
		ADDS_MY_FILES = 20, 
		ABOUT_PAGE = 22, ABOUT_UPDATE = 23, ABOUT_LEARN = 24, ABOUT_FEEDBACK = 25, ABOUT_SOURCE = 26;
	};
	
	public static enum LeftMenu {
		Me(new ItemBeanLeft(R.drawable.icon_people_smaller, "我"), ID.LEFT_ME),
		Printer(new ItemBeanLeft(R.drawable.icon_printer_smaller, "打印机"), ID.LEFT_PRINTER),
		People(new ItemBeanLeft(R.drawable.icon_people_smaller, "联系人"), ID.LEFT_PEOPLE),
		Settings(new ItemBeanLeft(R.drawable.icon_settings_smaller, "设置"), ID.LEFT_SETTINGS),
		More(new ItemBeanLeft(R.drawable.icon_music_downloader_smaller, "更多"), ID.LEFT_MORE),
		About(new ItemBeanLeft(R.drawable.icon_printer_smaller, "关于"), ID.LEFT_ABOUT);
		public ItemBeanLeft item;
		public int id;
		public LeftMenu(ItemBeanLeft item, int id) {
			this.item = item;
			this.id = id;
		}
	};
	
	public static enum Settings {
		MyInfo(new ItemBeanSettings("我的信息"), ID.ME_MY_INFO),
		SetInfo(new ItemBeanSettings("信息设置"), ID.ME_SET_INFO),
		MakeFriends(new ItemBeanSettings("新朋友"), ID.ME_MAKE_FRIENDS),
		NewRoom(new ItemBeanSettings("创建房间"), ID.ME_NEW_ROOM),
		NewUser(new ItemBeanSettings("新用户"), ID.ME_NEW_USER),
		Logout(new ItemBeanSettings("注销"), ID.ME_LOGOUT),
		Login(new ItemBeanSettings("重新登陆"), ID.ME_LOGIN),
		
		SetTheme(new ItemBeanSettings("主题"), ID.SETTINGS_THEME),
		SetServer(new ItemBeanSettings("服务器"), ID.SETTINGS_SERVER),
		SetFont(new ItemBeanSettings("字体"), ID.SETTINGS_FONT),
		SetSplash(new ItemBeanSettings("进入动画"), ID.SETTINGS_SPLASH),
		SetBg(new ItemBeanSettings("主页背景"), ID.SETTINGS_BG),
		
		SetDefaultPrinter(new ItemBeanSettings("默认打印机"), ID.PRINTER_DEFAULT),
		AddPrinter(new ItemBeanSettings("添加打印机"), ID.PRINTER_ADD),
		
		MyFiles(new ItemBeanSettings("我的文件"), ID.ADDS_MY_FILES),
		
		AboutUs(new ItemBeanSettings("关于我们"), ID.ABOUT_PAGE),
		AboutLearn(new ItemBeanSettings("快速上手"), ID.ABOUT_LEARN),
		Update(new ItemBeanSettings("检查更新"), ID.ABOUT_UPDATE),
		FeedBack(new ItemBeanSettings("使用反馈"), ID.ABOUT_FEEDBACK),
		Source(new ItemBeanSettings("Open source license"), ID.ABOUT_SOURCE);
		
		ItemBeanSettings item;
		int id;
		public Settings(ItemBeanSettings item, int id) {
			this.item = item;
			this.id = id;
		}
		public Settings(LeftMenu left) {
			item = new ItemBeanSettings(left.item.title);
			this.id = left.id;
		}
	}
	
	public static LeftMenu[] listLeft = {
		LeftMenu.Me, LeftMenu.Printer, LeftMenu.People, LeftMenu.Settings, LeftMenu.More, LeftMenu.About
	};
	
	public static Settings[] listMe = {
		Settings.Login, Settings.MyInfo, Settings.SetInfo, Settings.Logout,
	};
	
	public static Settings[] listPeople = {
		Settings.NewUser, Settings.MakeFriends, Settings.NewRoom, 
	};
	
	public static Settings[] listSettings = {
		Settings.SetTheme, Settings.SetServer, Settings.SetSplash, Settings.SetFont, Settings.SetBg, 
	};
	
	public static Settings[] listPrinter = {
		Settings.SetDefaultPrinter, Settings.AddPrinter,
	};
	
	public static Settings[] listAdds = {
		Settings.MyFiles, 
	};
	
	public static Settings[] listAbout = {
		Settings.Update, Settings.AboutUs, Settings.AboutLearn, Settings.FeedBack, Settings.Source, 
	};
}
