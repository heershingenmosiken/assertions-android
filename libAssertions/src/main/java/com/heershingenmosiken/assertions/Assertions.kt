/*
 * Copyright (c) 2017 Dekalo Stanislav. All rights reserved.
 */
package com.heershingenmosiken.assertions


/**
 * Main purpose is to add various assertions in IS_PRODUCTION mode.
 * And switch it off in production.
 *
 * Assertion not works until you will add AssertionHandler.
 *
 * Created by dekalo on 25.08.15.
 */
object Assertions : DefaultAssertions()