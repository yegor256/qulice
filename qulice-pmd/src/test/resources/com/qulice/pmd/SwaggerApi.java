/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package foo;

/**
 * Test class for swagger annotation.
 */
class SwaggerApi {
    /**
     * Get settings by name.
     * @param name Name.
     * @return Setting value.
     */
    @Operation(
        summary = "Get repository settings by name",
        description = """
              java
              multiline
              text
              block
            """,
        responses = {
            @ApiResponse(
                description = "Returns repository setting json",
                responseCode = "200",
                content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "404", description = "Repository not found")
        }
    )
    public final String setting(final String name) {
        throw RuntimeException("Not implemented");
    }
}
