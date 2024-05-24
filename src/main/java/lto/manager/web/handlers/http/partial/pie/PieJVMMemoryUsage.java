package lto.manager.web.handlers.http.partial.pie;

import org.xmlet.htmlapifaster.Div;

import lto.manager.common.Util;

public class PieJVMMemoryUsage {
	public static Void content(Div<?> view) {
		final int maxMemoryMB = (int) (Util.getJVMMaxMemory() / 1024 / 1024);
		final int allocatedMB = (int) (Util.getJVMAllocatedMemory() / 1024 / 1024);
		final int usedMemMB = (int) (Util.getUsedMemory() / 1024 / 1024);
		int p = (int) (((double)usedMemMB / (double)maxMemoryMB) * 100);

		final var po = new PieOptions(p, "JVM Memory Usage", "orange", "16rem", "-98px", "-124px",
				"""
				[In use] used from allocated, [Allocated] total request from OS,
				[Maximum] max memory for JVM set by -Xmx<br>
				""" + usedMemMB + " / " + maxMemoryMB + " * 100 = " + p + "%");
		po.details().add(PieOptions.getDetailsPair("In use:", usedMemMB + " MB"));
		po.details().add(PieOptions.getDetailsPair("Allocated:", allocatedMB + " MB"));
		po.details().add(PieOptions.getDetailsPair("Maximum:", maxMemoryMB + " MB"));
		view.of(v -> PieItem.content(v, po));
		return null;
	}
}
