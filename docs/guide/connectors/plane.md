# Plane Connector

Diveni can load projects and work items from a Plane workspace and write planning-poker estimates back to Plane.

The connector supports Plane Cloud and self-hosted Plane. It uses Plane's current `/work-items/` REST API and keeps the Plane API key in the Diveni backend.

## Create a Plane API key

Create an API key in Plane with access to the workspace and projects that Diveni should use. Treat the key as a secret and configure it only on the Diveni backend.

## Configuration

Add the following variables to `backend/.env` or the backend container environment:

```properties
PLANE_BASE_URL=https://plane.example.com
PLANE_WORKSPACE_SLUG=engineering
PLANE_API_KEY=plane_api_xxxxxxxxxxxxxxxxx
PLANE_ESTIMATE_VALUES=0,1,2,3,5,8,13,21
PLANE_ALLOW_DELETE=false
```

| Parameter | Description | Default |
|---|---|---|
| `PLANE_BASE_URL` | Plane instance root URL, without `/api/v1` | none |
| `PLANE_WORKSPACE_SLUG` | Workspace slug shown in the Plane URL | none |
| `PLANE_API_KEY` | Plane API key used by the Diveni backend | none |
| `PLANE_ESTIMATE_VALUES` | Eight comma-separated Diveni card values corresponding to Plane estimate slots `0` through `7` | `0,1,2,3,5,8,13,21` |
| `PLANE_ALLOW_DELETE` | Allow Diveni to permanently delete Plane work items | `false` |

The connector is enabled only when `PLANE_BASE_URL`, `PLANE_WORKSPACE_SLUG`, and `PLANE_API_KEY` are all set and `PLANE_ESTIMATE_VALUES` contains exactly eight values.

## Estimate mapping

Plane stores an estimate as a slot from `0` through `7`. Diveni displays the value at that position in `PLANE_ESTIMATE_VALUES`.

With the default configuration:

| Plane slot | Diveni value |
|---:|---:|
| 0 | 0 |
| 1 | 1 |
| 2 | 2 |
| 3 | 3 |
| 4 | 5 |
| 5 | 8 |
| 6 | 13 |
| 7 | 21 |

Use a card set compatible with the configured values. Diveni rejects an estimate that is not present in `PLANE_ESTIMATE_VALUES` rather than writing the wrong Plane slot.

## Use in Diveni

1. Restart the Diveni backend after adding the environment variables.
2. Start a session with an issue tracker.
3. Select **Connect to Plane**.
4. Select a Plane project.
5. Load and estimate its open work items.

Completed and archived Plane work items are excluded. Creating and editing work items is supported. Permanent deletion remains disabled unless `PLANE_ALLOW_DELETE=true` is explicitly configured.
