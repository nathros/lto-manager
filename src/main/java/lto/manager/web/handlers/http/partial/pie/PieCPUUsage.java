package lto.manager.web.handlers.http.partial.pie;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.DecimalFormat;

import org.xmlet.htmlapifaster.Div;

public class PieCPUUsage {
	private static DecimalFormat df = new DecimalFormat("0.00");

	public static Void content(Div<?> view) {
		OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
		double load = osBean.getSystemLoadAverage();
		int cores = Runtime.getRuntime().availableProcessors();
		double loadPercent = load / cores * 100;
		int loadInt = (int)(loadPercent);
		final var po = new PieOptions(loadInt, "CPU Usage", "green", "8rem", "-44px", "-60px",
				"""
				Total system load average
				""");
		po.details().add(PieOptions.getDetailsPair("Load total:", df.format(load)));
		po.details().add(PieOptions.getDetailsPair("CPU threads:", String.valueOf(cores)));
		view.of(v -> PieItem.content(v, po));
		return null;
	}
}

