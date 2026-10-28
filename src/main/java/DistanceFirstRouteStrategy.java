import java.util.Comparator;
import java.util.List;

public class DistanceFirstRouteStrategy implements RouteStrategy {

    @Override
    public Route selectRoute(
            List<Route> routes,
            DeliveryRequest request
    ) {
        return RouteFilters.validRoutes(routes, request)
                .stream()
                .min(Comparator.comparingDouble(
                        Route::distanceKm
                ))
                .orElseThrow();
    }

    @Override
    public String getStrategyName() {
        return "Distance First Route";
    }
}
