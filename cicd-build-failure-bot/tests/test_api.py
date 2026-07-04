from pathlib import Path
import sys
import unittest


PROJECT_ROOT = Path(__file__).resolve().parents[1]
if str(PROJECT_ROOT) not in sys.path:
    sys.path.insert(0, str(PROJECT_ROOT))

from app.main import create_app


class ApiTests(unittest.TestCase):
    def setUp(self) -> None:
        self.client = create_app().test_client()

    def test_health(self) -> None:
        response = self.client.get("/health")
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.get_json()["status"], "ok")

    def test_predict_returns_risk_score(self) -> None:
        response = self.client.post(
            "/predict",
            json={
                "changed_files": 55,
                "additions": 1500,
                "deletions": 250,
                "previous_failed_runs_7d": 3,
                "flaky_tests_7d": 4,
                "dependency_files_changed": 1,
                "cache_hit_rate": 0.25,
                "branch": "feature/payment",
            },
        )
        body = response.get_json()
        self.assertEqual(response.status_code, 200)
        self.assertIn("failure_probability", body)
        self.assertIn(body["risk_level"], {"low", "medium", "high"})


if __name__ == "__main__":
    unittest.main()
