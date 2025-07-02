/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.entity.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Test model for dependency-not-matches-exclude integration test.
 * @since 1.0
 */
@Entity
@Table(name = "TEST_TABLE")
public class TestModel {
}
