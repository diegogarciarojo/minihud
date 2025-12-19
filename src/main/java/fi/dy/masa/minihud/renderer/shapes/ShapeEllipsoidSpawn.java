package fi.dy.masa.minihud.renderer.shapes;

import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import fi.dy.masa.malilib.util.JsonUtils;
import fi.dy.masa.malilib.util.Quadrant;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.minihud.config.Configs;
import fi.dy.masa.minihud.util.shape.SphereUtils;

public class ShapeEllipsoidSpawn extends ShapeSpawnSphere
{
    private double radiusY = 24.0;
    private double radiusZ = 24.0;

    public ShapeEllipsoidSpawn()
    {
        super(ShapeType.ELLIPSOID_SPAWN, Configs.Colors.SHAPE_ADJUSTABLE_SPAWN_SPHERE.getColor(), 24.0);
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate("minihud.label.shapes.ellipsoid_spawn");
    }

    public double getRadiusY()
    {
        return this.radiusY;
    }

    public double getRadiusZ()
    {
        return this.radiusZ;
    }

    public void setRadiusY(double radiusY)
    {
        this.radiusY = radiusY;
        this.setNeedsUpdate();
    }

    public void setRadiusZ(double radiusZ)
    {
        this.radiusZ = radiusZ;
        this.setNeedsUpdate();
    }

    @Override
    protected SphereUtils.RingPositionTest getPositionTest()
    {
        return this::isPositionOnOrInsideEllipsoidRing;
    }

    protected boolean isPositionOnOrInsideEllipsoidRing(int x, int y, int z, Direction outSide)
    {
        Vec3d effectiveCenter = this.getEffectiveCenter();
        double radiusX = this.getRadius();
        double radiusY = this.getRadiusY();
        double radiusZ = this.getRadiusZ();
        double posX = x + 0.5;
        double posY = y + 1;
        double posZ = z + 0.5;

        if (this.getUseCornerQuadrants())
        {
            Vec3d quadrantCenter = this.quadrantCenters[Quadrant.getQuadrant(x, z, effectiveCenter).ordinal()];
            return this.isPositionWithinEllipsoidAt(posX, posY, posZ, quadrantCenter, radiusX, radiusY, radiusZ) ||
                   this.isPositionWithinEllipsoidAt(posX, posY, posZ, effectiveCenter, radiusX, radiusY, radiusZ);
        }
        else
        {
            double margin = this.getMargin();
            double centerX = MathHelper.clamp(posX, effectiveCenter.x - margin, effectiveCenter.x + margin);
            double centerY = effectiveCenter.y;
            double centerZ = MathHelper.clamp(posZ, effectiveCenter.z - margin, effectiveCenter.z + margin);
            return this.isPositionWithinEllipsoidAt(posX, posY, posZ, new Vec3d(centerX, centerY, centerZ), radiusX, radiusY, radiusZ);
        }
    }

    private boolean isPositionWithinEllipsoidAt(double posX, double posY, double posZ, Vec3d center,
                                                 double radiusX, double radiusY, double radiusZ)
    {
        double dx = (posX - center.x) / (radiusX <= 0.0 ? 1.0 : radiusX);
        double dy = (posY - center.y) / (radiusY <= 0.0 ? 1.0 : radiusY);
        double dz = (posZ - center.z) / (radiusZ <= 0.0 ? 1.0 : radiusZ);
        double distanceSquared = dx * dx + dy * dy + dz * dz;
        return distanceSquared <= 1.0;
    }

    @Override
    public List<String> getWidgetHoverLines()
    {
        List<String> lines = super.getWidgetHoverLines();
        lines.add(3, StringUtils.translate("minihud.gui.hover.shape.radius_y_value", d2(this.radiusY)));
        lines.add(4, StringUtils.translate("minihud.gui.hover.shape.radius_z_value", d2(this.radiusZ)));
        return lines;
    }

    @Override
    public JsonObject toJson()
    {
        JsonObject obj = super.toJson();
        obj.add("radius_y", new JsonPrimitive(this.radiusY));
        obj.add("radius_z", new JsonPrimitive(this.radiusZ));
        return obj;
    }

    @Override
    public void fromJson(JsonObject obj)
    {
        super.fromJson(obj);
        this.radiusY = JsonUtils.getDoubleOrDefault(obj, "radius_y", 24.0);
        this.radiusZ = JsonUtils.getDoubleOrDefault(obj, "radius_z", 24.0);
    }
}
