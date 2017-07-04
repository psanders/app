/**
 * Copyright (C) 2017 <fonosterteam@fonoster.com> https://fonoster.com
 *
 * <p>This file is part of Fonoster
 *
 * <p>Fonoster can not be copied and/or distributed without the express permission of Fonoster's
 * copyright owners.
 */
package com.fonoster.voice.asr;

import com.fonoster.annotations.Since;

@Since("1.0")
public interface ASR {
  void transcribe(String file, BluemixASR.JSFunc callback);
}
