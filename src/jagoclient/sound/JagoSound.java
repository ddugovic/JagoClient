package jagoclient.sound;

import jagoclient.Global;
import rene.util.sound.SoundList;

public class JagoSound
{
	static SoundList SL = new SoundList();

	static synchronized public void play (String file, String simplefile,
		boolean beep)
	{
		if (Global.getParameter("nosound", true)) return;
		if (Global.getParameter("beep", true))
		{
			if (beep) SoundList.beep();
			return;
		}
		if (Global.getParameter("simplesound", true)) file = simplefile;
		if (file.equals("")) return;
		if (SL.busy()) return;
        SL.play("/au/" + file + ".wav");
	}

	static public void play (String file)
	{
		if (SL.busy()) return;
		play(file, "wip", false);
	}
}
