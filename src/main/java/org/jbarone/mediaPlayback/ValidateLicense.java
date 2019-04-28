package org.jbarone.mediaPlayback;

import org.jbarone.mediaPlayback.licensing.LicenseValidator;
import org.jbarone.utils.ResourceUtils;

import java.io.File;

public class ValidateLicense {
  public static void main(String[] args) throws Exception {
    LicenseValidator validator = new LicenseValidator();

    if (args.length != 3) {
      System.out.println("Usage: java org.jbarone.mediaPlayback.ValidateLicense <member> <device> <viewable>");
      System.out.println("E.g.: ValidateLicense jsmith samsung_GS9 StarTrekIV");
      return;
    }

    File memberFile = ResourceUtils.loadResourceFile("members/" + args[0] + ".json");
    File deviceFile = ResourceUtils.loadResourceFile("devices/" + args[1] + ".json");
    File viewableFile = ResourceUtils.loadResourceFile("viewables/" + args[2] + ".json");

    Member member = Member.fromJsonFile(memberFile);
    Device device = Device.fromJsonFile(deviceFile);
    Viewable viewable = Viewable.fromJsonFile(viewableFile);

    validator.validateForPlayback(member, device, viewable);
  }
}
