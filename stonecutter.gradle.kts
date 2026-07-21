plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.1" /* [SC] DO NOT EDIT */

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    swaps["mod_version"] = "\"${property("mod.version")}\";"
    swaps["minecraft"] = "\"${node.metadata.version}\";"
    dependencies["fapi"] = node.project.property("deps.fabric_api") as String
    
    replacements {
        string {
            direction = eval(node.metadata.version, ">=26.1")
            replace("ResourceLocation", "Identifier")
            replace("net.minecraft.client.gui.GuiGraphics", "net.minecraft.client.gui.GuiGraphicsExtractor")
            replace("GuiGraphics.class", "GuiGraphicsExtractor.class")
            replace("GuiGraphics guiGraphics", "GuiGraphicsExtractor guiGraphics")
            replace("GuiGraphics graphics", "GuiGraphicsExtractor graphics")
            replace("renderWidget", "extractWidgetRenderState")
            replace("renderBackground", "extractBackground")
            replace("super.render(", "super.extractRenderState(")
            replace("public void render(", "public void extractRenderState(")
        }
    }
}
