import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class StrategySelector {

    private final Map<DeliveryPriority, RouteStrategy> strategies =
            new EnumMap<>(DeliveryPriority.class);

    public StrategySelector() {
        register(DeliveryPriority.URGENT,
                new FastestRouteStrategy());

        register(DeliveryPriority.ECONOMY,
                new CheapestRouteStrategy());

        register(DeliveryPriority.ECO,
                new EcoFriendlyRouteStrategy());

        register(DeliveryPriority.STANDARD,
                new BalancedRouteStrategy(
                        0.4, 0.3, 0.1, 0.2
                ));
    }

    public void register(
            DeliveryPriority priority,
            RouteStrategy strategy
    ) {
        strategies.put(
                Objects.requireNonNull(priority),
                Objects.requireNonNull(strategy)
        );
    }

    public RouteStrategy selectStrategy(
            DeliveryRequest request
    ) {
        Objects.requireNonNull(request, "Request cannot be null");

        RouteStrategy strategy = strategies.get(request.priority());

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No strategy for priority: " + request.priority()
            );
        }

        return strategy;
    }
}
