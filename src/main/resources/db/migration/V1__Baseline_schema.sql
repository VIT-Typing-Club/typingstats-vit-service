-- V1__Baseline_schema.sql

CREATE TABLE "User" (
    "discordId" text NOT NULL,
    username text NOT NULL,
    displayname text,
    "avatarUrl" text,
    "collegeEmail" text,
    "collegeVerified" boolean NOT NULL DEFAULT false,
    "collegeCode" text,
    "mtVerified" boolean NOT NULL DEFAULT false,
    "mtUrl" text,
    "linkedinUrl" text,
    "XUrl" text,
    "instagramUrl" text,
    "githubUrl" text,
    "createdAt" timestamp(3) without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "User_pkey" PRIMARY KEY ("discordId")
);

CREATE TABLE "Score" (
    id text NOT NULL,
    "userId" text NOT NULL,
    "testType" text NOT NULL,
    wpm double precision NOT NULL,
    accuracy double precision,
    raw double precision,
    "createdAt" timestamp(3) without time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "Score_pkey" PRIMARY KEY (id),
    CONSTRAINT "Score_userId_testType_key" UNIQUE ("userId", "testType"),
    CONSTRAINT "Score_userId_fkey" FOREIGN KEY ("userId") REFERENCES "User"("discordId") ON UPDATE CASCADE ON DELETE RESTRICT
);