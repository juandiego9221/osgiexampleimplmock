package pe.com.jdmm21.felix.bookshelf.inventory.impl.mock.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import pe.com.jdmm21.felix.bookshelf.inventory.api.BookInventory;
import pe.com.jdmm21.felix.bookshelf.inventory.impl.mock.BookInventoryMockImpl;

public class BookInventoryMockImplActivator implements BundleActivator {

	private ServiceRegistration reg = null;

	@Override
	public void start(BundleContext context) throws Exception {
		System.out.println("\nStarting book inventory mock impl");
		this.reg = context.registerService(BookInventory.class.getName(), new BookInventoryMockImpl(), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("\nStopping book inventory mock impl");
		if (this.reg != null) {
			context.ungetService(reg.getReference());
			this.reg = null;
		}
	}

}
