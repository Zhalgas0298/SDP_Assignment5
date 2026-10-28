import java.util.Comparator;
import java.util.List;

public class CheapestRouteStrategy implements RouteStrategy {

    @Override
    public Route selectRoute(
            List<Route> routes,
            DeliveryRequest request
    ) {
        return RouteFilters.validRoutes(routes, request)
                .stream()
                .min(Comparator.comparingDouble(
                        r -> RouteCosts.totalCost(r, request)
                ))
                .orElseThrow();
    }

    @Override
    public String getStrategyName() {
        return "Cheapest Route";
    }
}
