package to.wetf.hale.progen.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import eu.esdihumboldt.hale.common.core.io.supplier.LocatableOutputSupplier;

public class WriteOnceOutputSupplier implements LocatableOutputSupplier<OutputStream> {

  private final OutputStream out;
  
  public WriteOnceOutputSupplier(OutputStream out) {
    super();
    this.out = out;
  }

  @Override
  public OutputStream getOutput() throws IOException {
    return out;
  }

  @Override
  public URI getLocation() {
    return null;
  }

}
