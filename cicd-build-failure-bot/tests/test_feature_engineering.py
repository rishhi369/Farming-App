from pathlib import Path
import sys
import unittest


PROJECT_ROOT = Path(__file__).resolve().parents[1]
if str(PROJECT_ROOT) not in sys.path:
    sys.path.insert(0, str(PROJECT_ROOT))

from app.feature_engineering import github_event_to_features, normalize_features


class FeatureEngineeringTests(unittest.TestCase):
    def test_normalize_features_adds_defaults(self) -> None:
        features = normalize_features({"changed_files": "12", "cache_hit_rate": 1.8})
        self.assertEqual(features["changed_files"], 12)
        self.assertEqual(features["cache_hit_rate"], 1.0)
        self.assertEqual(features["workflow_name"], "ci")

    def test_pull_request_event_extracts_features(self) -> None:
        payload = {
            "repository": {"full_name": "acme/api", "language": "Python"},
            "pull_request": {
                "number": 8,
                "changed_files": 20,
                "additions": 500,
                "deletions": 80,
                "commits": 3,
                "author_association": "MEMBER",
                "head": {"ref": "feature/new-cache", "sha": "abc"},
                "labels": [{"name": "hotfix"}],
                "created_at": "2026-06-20T10:00:00Z",
            },
            "files": [{"filename": "requirements.txt"}, {"filename": "app/main.py"}],
        }
        features = github_event_to_features("pull_request", payload)
        self.assertIsNotNone(features)
        self.assertEqual(features["branch"], "feature")
        self.assertEqual(features["dependency_files_changed"], 1)
        self.assertEqual(features["label_hotfix"], "true")


if __name__ == "__main__":
    unittest.main()
