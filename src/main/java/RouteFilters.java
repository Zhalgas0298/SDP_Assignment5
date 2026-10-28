import java.util.List;
import java.util.Objects;

public class RouteFilters {

    public static List<Route> validRoutes(
            List<Route> routes,
            DeliveryRequest request
    ) {
        Objects.requireNonNull(routes, "Routes cannot be null");
        Objects.requireNonNull(request, "Request cannot be null");
        Objects.requireNonNull(request.priority(), "Priority cannot be null");

        if (request.maxBudget() < 0
                || !Double.isFinite(request.maxBudget())
                || request.maxDeliveryMinutes() < 0) {
            throw new IllegalArgumentException(
                    "Invalid delivery request"
            );
        }

        List<Route> valid = routes.stream()
                .filter(Objects::nonNull)
                .filter(r -> r.distanceKm() >= 0
                        && Double.isFinite(r.distanceKm()))
                .filter(r -> r.cost() >= 0
                        && Double.isFinite(r.cost()))
                .filter(r -> r.carbonEmission() >= 0
                        && Double.isFinite(r.carbonEmission()))
                .filter(r -> r.estimatedMinutes() >= 0)
                .filter(r -> r.trafficLevel() >= 0)
                .filter(Route::available)
                .filter(r -> RouteCosts.totalCost(r, request)
                        <= request.maxBudget())
                .filter(r -> r.estimatedMinutes()
                        + r.trafficLevel() * 5L
                        <= request.maxDeliveryMinutes())
                .toList();

        if (valid.isEmpty()) {
            throw new NoSuitableRouteException(
                    "No suitable route found"
            );
        }

        return valid;
    }
}
