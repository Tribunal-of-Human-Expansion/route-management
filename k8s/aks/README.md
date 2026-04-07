# AKS Deployment (Workload Identity + Key Vault)

This folder contains AKS manifests for deploying `route-management` into the same cluster as `booking-service` and `journey-compatibility`.

## In-cluster service communication

`booking-service` should call route-management using:

- `http://route-management-svc`

Do not use external IPs for service-to-service traffic inside the cluster.

## Workload identity note

This manifest uses the existing `booking-service` ServiceAccount in `default` namespace.
It allows route-management to reuse the already configured federated identity mapping.

## Required Key Vault secrets

Create these secrets in `ds-gtbs-kv-prod`:

- `route-datasource-url`
- `route-datasource-username`
- `route-datasource-password`
- `spring-data-redis-host`
- `spring-data-redis-port`
- `spring-data-redis-password`
- `spring-kafka-bootstrap-servers`

## Deploy

```bash
kubectl apply -k k8s/aks
kubectl rollout status deployment/route-management -n default
```

## Verify

```bash
kubectl get pods -n default -l app=route-management
kubectl get svc route-management-svc -n default
kubectl logs -n default deploy/route-management --tail=100
kubectl get secret route-management-kv-synced -n default
```

## Smoke test from inside cluster

```bash
kubectl run curl-test --rm -i --tty --image=curlimages/curl --restart=Never -- \
  curl -sS -X POST http://route-management-svc/api/routes/decompose \
  -H "Content-Type: application/json" \
  -d '{"origin":"DUBLIN","destination":"GALWAY"}'
```
