import java.util.Comparator;
import java.util.List;

public class EcoFriendlyRouteStrategy implements RouteStrategy {

    @Override
    public Route selectRoute(
            List<Route> routes,
            DeliveryRequest request
    ) {
        return RouteFilters.validRoutes(routes, request)
                .stream()
                .min(Comparator.comparingDouble(
                        r -> r.carbonEmission() * 0.8
                                + r.distanceKm() * 0.2
                ))
                .orElseThrow();
    }

    @Override
    public String getStrategyName() {
        return "Eco Friendly Route";
    }
}
