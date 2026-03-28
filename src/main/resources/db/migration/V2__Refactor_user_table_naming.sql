ALTER TABLE "User" RENAME TO users;

ALTER TABLE users RENAME COLUMN "discordId" TO discord_id;
ALTER TABLE users RENAME COLUMN "avatarUrl" TO avatar_url;
ALTER TABLE users RENAME COLUMN "collegeEmail" TO college_email;
ALTER TABLE users RENAME COLUMN "collegeVerified" TO college_verified;
ALTER TABLE users RENAME COLUMN "collegeCode" TO college_code;
ALTER TABLE users RENAME COLUMN "mtVerified" TO mt_verified;
ALTER TABLE users RENAME COLUMN "mtUrl" TO mt_url;
ALTER TABLE users RENAME COLUMN "linkedinUrl" TO linkedin_url;
ALTER TABLE users RENAME COLUMN "XUrl" TO x_url;
ALTER TABLE users RENAME COLUMN "instagramUrl" TO instagram_url;
ALTER TABLE users RENAME COLUMN "githubUrl" TO github_url;
ALTER TABLE users RENAME COLUMN "createdAt" TO created_at;

ALTER TABLE users RENAME CONSTRAINT "User_pkey" TO users_pkey;

ALTER TABLE "Score" RENAME COLUMN "userId" TO user_id;
ALTER TABLE "Score" RENAME COLUMN "testType" TO test_type;
ALTER TABLE "Score" RENAME COLUMN "createdAt" TO created_at;

ALTER TABLE "Score" DROP CONSTRAINT "Score_userId_fkey";

ALTER TABLE "Score"
ADD CONSTRAINT score_user_id_fkey
FOREIGN KEY (user_id)
REFERENCES users(discord_id)
ON UPDATE CASCADE
ON DELETE RESTRICT;

ALTER INDEX "Score_userId_testType_key"
RENAME TO score_user_id_test_type_key;

ALTER TABLE "Score" RENAME TO scores;

ALTER TABLE scores RENAME CONSTRAINT "Score_pkey" TO scores_pkey;
