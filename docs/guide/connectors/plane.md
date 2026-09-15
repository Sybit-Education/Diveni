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
PLANE_ALLOW_DELETE=false
```

| Parameter | Description | Default |
|---|---|---|
| `PLANE_BASE_URL` | Plane instance root URL, without `/api/v1` | none |
| `PLANE_WORKSPACE_SLUG` | Workspace slug shown in the Plane URL | none |
| `PLANE_API_KEY` | Plane API key used by the Diveni backend | none |
| `PLANE_ALLOW_DELETE` | Allow Diveni to permanently delete Plane work items | `false` |

The connector is enabled only when `PLANE_BASE_URL`, `PLANE_WORKSPACE_SLUG`, and `PLANE_API_KEY` are all set.

## Estimate mapping

Plane stores each configured estimate as an estimate-point record with its own UUID. Diveni reads the active estimate system for the selected project, loads its estimate points, and maps values by their displayed value.

For example, when a Plane project uses Fibonacci values such as `1`, `2`, `3`, `5`, `8`, `13`, `21`, `34`, and `55`, selecting `13` in Diveni causes the connector to send the UUID of Plane's estimate point whose value is `13`.

No fixed slot table is used. This allows the connector to work with:

- Fibonacci
- Linear
- Squares
- Custom point systems
- Category systems, provided the Diveni card values match the Plane category values

Choose a Diveni card set whose values match the selected Plane project's estimate system. Diveni rejects a value that does not exist in that project's active Plane estimate system rather than writing the wrong estimate.

## Use in Diveni

1. Restart the Diveni backend after adding the environment variables.
2. Start a session with an issue tracker.
3. Select **Connect to Plane**.
4. Select a Plane project.
5. Load and estimate its open work items.
6. After voting, select the agreed final estimate from the dropdown beside the story title.

Completed and archived Plane work items are excluded. Creating and editing work items is supported. Permanent deletion remains disabled unless `PLANE_ALLOW_DELETE=true` is explicitly configured.
