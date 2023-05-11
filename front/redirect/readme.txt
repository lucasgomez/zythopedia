This whole redirect folder with it's php script in index.html serve as a proxy for prod env to avoid the mixed content error due to the front being in HTTPS but the backend in HTTP. This is a temporary solution (meaning it will stay forever) before a certificate can be assigned to the backend.
When deploying the front app, be sure to have this folder at the root of the app files.
