package com.andyadc.scan;

import com.andyadc.imported.LocalDateConfiguration;
import com.andyadc.imported.ZonedDateConfiguration;
import com.andyadc.summer.annotation.ComponentScan;
import com.andyadc.summer.annotation.Import;

@ComponentScan
@Import({ZonedDateConfiguration.class, LocalDateConfiguration.class})
public class ScanApplication {
}
