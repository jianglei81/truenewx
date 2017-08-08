package org.truenewx.web.version;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.truenewx.core.Strings;
import org.truenewx.core.version.VersionReader;

/**
 * VersionController
 *
 * @author jianglei
 * @since JDK 1.8
 */
@Controller
@RequestMapping("/version")
public class VersionController {
    @Autowired(required = false)
    private VersionReader versionReader;

    @RequestMapping("/build")
    @ResponseBody
    public String build() {
        if (this.versionReader != null) {
            return this.versionReader.getVersion(true);
        }
        return Strings.EMPTY;
    }

    @RequestMapping("/release")
    @ResponseBody
    public String release() {
        if (this.versionReader != null) {
            return this.versionReader.getVersion(false);
        }
        return Strings.EMPTY;
    }

}
